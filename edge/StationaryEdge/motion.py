import time

import cv2
import numpy as np
from bluepy.btle import Peripheral, Advertisement, BTLEException

import picamera
import picamera.array

# Initialize the PiCamera
camera = picamera.PICamera()
camera.resolution = (640, 480)
camera.framerate = 24
time.sleep(2)  # Allowing the camera to warm up

# Initialize the camera for capturing images
raw_capture = picamera.array.PiRGBArray(camera)

# Read the first frame to get the shape of the frames
camera.capture(raw_capture, format="bgr")
first_frame = raw_capture.array

# Convert the first frame to grayscale
first_frame_gray = cv2.cvtColor(first_frame, cv2.COLOR_BGR2GRAY)

# Apply Gaussian blur to reduce noise
first_frame_gray = cv2.GaussianBlur(first_frame_gray, (21, 21), 0)

# Function to calculate confidence value
def calculate_confidence(frame_diff, thresh):
    # Flatten the thresholded image to a 1D array
    thresh_flat = thresh.flatten()
    
    # Calculate the number of non-zero pixels in the thresholded image
    non_zero_pixels = np.count_nonzero(thresh_flat)
    
    # Normalize to give a value between 0 and 1
    confidence = non_zero_pixels / thresh_flat.size
    
    return confidence

# Initialize BLE advertisement
def broadcast_confidence(confidence):
    try:
        # Create a Peripheral device and Advertisement
        advertisement = Advertisement()
        advertisement.addServiceUUID(0x1812)  # UUID for the environmental sensing service (example)
        
        # Broadcast the confidence value as part of the advertisement data
        advertisement.addData(0x20, f"Confidence: {confidence:.2f}")  
        
        # Start the advertisement
        advertisement.start()
        print(f"Broadcasting Confidence: {confidence:.2f}")
        
    except BTLEException as e:
        print(f"Error broadcasting BLE: {str(e)}")

# Loop to capture video frames
while True:
    # Capture the current frame
    camera.capture(raw_capture, format="bgr")
    frame = raw_capture.array

    # Convert the current frame to grayscale
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Apply Gaussian blur to the grayscale frame
    gray = cv2.GaussianBlur(gray, (21, 21), 0)

    # Compute the absolute difference between the current frame and the first frame
    frame_diff = cv2.absdiff(first_frame_gray, gray)

    # Threshold the difference to detect movement
    _, thresh = cv2.threshold(frame_diff, 25, 255, cv2.THRESH_BINARY)

    # Dilate the threshold image to fill in holes
    thresh = cv2.dilate(thresh, None, iterations=2)

    # Calculate confidence score
    confidence = calculate_confidence(frame_diff, thresh)

    # Find contours in the thresholded image
    contours, _ = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # Loop through the contours to find areas of movement
    for contour in contours:
        if cv2.contourArea(contour) < 500:  
            continue

        # Get the bounding box of the moving object
        (x, y, w, h) = cv2.boundingRect(contour)

        # Draw a rectangle around the detected object
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

        # Display confidence value on screen
        cv2.putText(frame, f'Confidence: {confidence:.2f}', (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)

        # Broadcast the confidence value via BLE
        broadcast_confidence(confidence)

        # Optional: Print movement detected message and confidence
        print(f'Movement detected! Confidence: {confidence:.2f}')

    # Display the resulting frame
    cv2.imshow('Motion Detection with Confidence', frame)

    # Update the first frame to be the current one for the next loop
    first_frame_gray = gray

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the camera and close any OpenCV windows
camera.close()
cv2.destroyAllWindows()
