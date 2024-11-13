import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.dcis2.Model.AnimalCategory
import com.example.dcis2.R
import androidx.core.content.ContextCompat

class AnimalAdapter(
    private val context: Context,
    private val categories: List<AnimalCategory>,
    private val selectedItems: MutableSet<String>
) : BaseAdapter() {

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Any = categories[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_animal, parent, false)
        val category = categories[position]

        val imageView = view.findViewById<ImageView>(R.id.animalImage)
        val textView = view.findViewById<TextView>(R.id.animalText)

        imageView.setImageResource(category.imageResId)
        textView.text = category.name

        // Update view appearance based on selection
        if (selectedItems.contains(category.name)) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedItemBackground))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.defaultItemBackground))
        }

        return view
    }
}