import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.Model.AnimalCategory
import com.example.dcis2.R

class AnimalPreferenceActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var selectedCategories: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_preference)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Load previously selected categories if available
        selectedCategories = sharedPreferences.getStringSet("PreferredAnimalCategories", mutableSetOf())!!.toMutableSet()

        // List of animal categories with drawable resources (replace with actual drawable names)
        val animalCategories = listOf(
            AnimalCategory("Growing Wild", R.drawable.growing_wild_icon),
            AnimalCategory("Birds", R.drawable.birds_icon),
            AnimalCategory("Sea Creatures", R.drawable.sea_creatures_icon),
            AnimalCategory("Predators", R.drawable.predators_icon),
            AnimalCategory("Reptiles", R.drawable.reptiles_icon),
            AnimalCategory("Australian Natives", R.drawable.australian_natives_icon),
            AnimalCategory("Rainforest", R.drawable.rainforest_icon),
            AnimalCategory("Apes and Monkeys", R.drawable.apes_monkeys_icon)
        )

        val gridView = findViewById<GridView>(R.id.gridViewAnimalCategories)
        val adapter = AnimalAdapter(this, animalCategories, selectedCategories)
        gridView.adapter = adapter

        // Handle item clicks for multiple selection
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCategory = animalCategories[position].name
            if (selectedCategories.contains(selectedCategory)) {
                selectedCategories.remove(selectedCategory)
            } else {
                selectedCategories.add(selectedCategory)
            }
            adapter.notifyDataSetChanged() // Refresh the grid view
        }

        // Save preferences button (optional)
        val btnSavePreferences = findViewById<Button>(R.id.btnSavePreferences)
        btnSavePreferences.setOnClickListener {
            saveAnimalPreferences()
            Toast.makeText(this, "Preferences Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAnimalPreferences() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("PreferredAnimalCategories", selectedCategories)
        editor.apply()
    }
}
