import cv2
import json
import asyncio
import numpy as np
from datetime import datetime
from picamera2 import Picamera2
from bleak import BleakAdvertiser

# Model and config files
MODEL_PATH = 'ssd_mobilenet_v1_coco_2017_11_17/frozen_inference_graph.pb'
CONFIG_PATH = 'ssd_mobilenet_v1_coco.pbtxt'
HASH_KEY = 'a4b17238dd395a91'

# Using Elephant classe from the COCO model. 
ANIMAL_CLASSES = {
    22: 'elephant'
}

BLE_NAME = 'elephant_enc'
DETECTION_THRESHOLD = 0.5

# Loading the model
net = cv2.dnn.readNetFromTensorflow(MODEL_PATH, CONFIG_PATH)

# Broadcasting the context information in JSON format every 5 seconds.
async def broadcast_json(json_data):
    print(f'Broadcasting over BLE: {json_data}')
    async with BleakAdvertiser() as advertiser:
        await advertiser.advertise(name=BLE_NAME, manufacturer_data={0xFFFF: json_data.encode()})
        await asyncio.sleep(5)

# Detecting the specific animal.
# Birds will be discarded.
def detect_animals(frame):
    h, w = frame.shape[:2]
    blob = cv2.dnn.blobFromImage(frame, size=(300, 300), swapRB=True, crop=False)
    net.setInput(blob)
    output = net.forward()

    animals = []

    for detection in output[0, 0, :, :]:
        confidence = float(detection[2])
        class_id = int(detection[1])

        if confidence > DETECTION_THRESHOLD and class_id in ANIMAL_CLASSES:
            box = detection[3:7] * np.array([w, h, w, h])
            (x1, y1, x2, y2) = box.astype('int')
            animals.append({
                'label': ANIMAL_CLASSES[class_id],
                'confidence': confidence,
                'box': (x1, y1, x2, y2)
            })

    return animals

# Drawing the detection boxes.
def draw_detections(frame, animals):
    for a in animals:
        x1, y1, x2, y2 = a['box']
        label = f"{a['label']} ({a['confidence']:.2f})"
        cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
        cv2.putText(frame, label, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)

# Preparing the JSON for broadcasting.
def create_json(animals):
    confidence_avg = round(np.mean([a['confidence'] for a in animals]), 2) if animals else 0.0
    return json.dumps({
        'hashkey': HASH_KEY,
        'confidence': confidence_avg,
        'multiple': len(animals) > 1,
        'timestamp': datetime.now(datetime.timezone.utc).isoformat(),
        'movement': True if confidence_avg > DETECTION_THRESHOLD else False
    })

def main():
    picam2 = Picamera2()
    picam2.preview_configuration.main.size = (640, 480)
    picam2.preview_configuration.main.format = 'RGB888'
    picam2.configure('preview')
    picam2.start()

    while True:
        frame = picam2.capture_array()

        animals = detect_animals(frame)

        if animals:
            draw_detections(frame, animals)
            data = create_json(animals)
            asyncio.run(broadcast_json(data))

        cv2.imshow('Animal Motion Detection', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    picam2.stop()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
