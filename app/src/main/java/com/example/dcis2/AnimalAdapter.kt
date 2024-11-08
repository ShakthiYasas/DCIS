package com.example.dcis2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class AnimalAdapter(
    private val context: Context,
    private val animalNames: Array<String>,
    private val animalImages: Array<Int>
) : BaseAdapter() {

    override fun getCount(): Int = animalNames.size

    override fun getItem(position: Int): Any = animalNames[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // Initialize the button and set up the click listener
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.grid_item_animal, parent, false
        )

        val imageView: ImageView = view.findViewById(R.id.animal_image)
        val textView: TextView = view.findViewById(R.id.animal_name)

        imageView.setImageResource(animalImages[position])
        textView.text = animalNames[position]

        return view
    }


}
