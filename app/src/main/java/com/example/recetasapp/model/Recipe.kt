package com.example.recetasapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Step(
    val description: String,
    val timeMinutes: Int? = null
) : Parcelable

@Parcelize
data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val image: String ,
    val prepTime: Int,
    val servings: Int,
    val ingredients: List<String>,
    val steps: List<Step>
) : Parcelable


// Mock data
val DEFAULT_RECIPES = listOf(
    Recipe(
        id = "1",
        name = "Pasta Carbonara",
        description = "Clásica receta italiana con huevo, queso parmesano y panceta crujiente",
        image = "https://images.unsplash.com/photo-1588013273468-315fd88ea34c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXN0YSUyMGNhcmJvbmFyYXxlbnwxfHx8fDE3Njg0ODY3NTF8MA&ixlib=rb-4.1.0&q=80&w=1080",
        prepTime = 25,
        servings = 4,
        ingredients = listOf(
            "400g de pasta (espagueti o linguini)",
            "200g de panceta o tocino en cubos",
            "4 huevos grandes",
            "100g de queso parmesano rallado",
            "Pimienta negra recién molida",
            "Sal al gusto"
        ),
        steps = listOf(
            Step("Pon a hervir agua con sal en una olla grande y cocina la pasta según las instrucciones del paquete hasta que esté al dente.", 10),
            Step("Mientras tanto, en un bol grande, bate los huevos con el queso parmesano rallado y una generosa cantidad de pimienta negra."),
            Step("En una sartén grande, cocina la panceta a fuego medio hasta que esté dorada y crujiente.", 7),
            Step("Cuando la pasta esté lista, reserva 1 taza del agua de cocción y escurre la pasta."),
            Step("Retira la sartén del fuego y añade la pasta caliente a la panceta. Mezcla bien.", 2),
            Step("Añade la mezcla de huevo y queso a la pasta caliente, mezclando rápidamente."),
            Step("Si la salsa está muy espesa, añade poco a poco el agua de cocción reservada."),
            Step("Sirve inmediatamente con más queso parmesano y pimienta negra por encima.")
        )
    ),
    Recipe(
        id = "2",
        name = "Tarta de Chocolate",
        description = "Delicioso postre de chocolate intenso con textura húmeda y esponjosa",
        image = "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjaG9jb2xhdGUlMjBjYWtlfGVufDF8fHx8MTc2ODU3MDQ1MHww&ixlib=rb-4.1.0&q=80&w=1080",
        prepTime = 50,
        servings = 8,
        ingredients = listOf(
            "200g de chocolate negro",
            "150g de mantequilla",
            "4 huevos",
            "200g de azúcar",
            "100g de harina",
            "1 cucharadita de extracto de vainilla",
            "Una pizca de sal"
        ),
        steps = listOf(
            Step("Precalienta el horno a 180°C y engrasa un molde redondo de 22cm.", 10),
            Step("Derrite el chocolate con la mantequilla al baño maría.", 5),
            Step("En un bol grande, bate los huevos con el azúcar hasta que la mezcla esté espumosa.", 5),
            Step("Añade el chocolate derretido a la mezcla de huevos y bate suavemente."),
            Step("Incorpora la harina tamizada, la vainilla y la sal."),
            Step("Vierte la mezcla en el molde y hornea.", 35),
            Step("Deja enfriar en el molde antes de desmoldar.", 10)
        )
    ),
    Recipe(
        id = "3",
        name = "Ensalada Fresca de Verano",
        description = "Ensalada nutritiva y colorida con vegetales frescos",
        image = "https://images.unsplash.com/photo-1620019989479-d52fcedd99fe?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMHNhbGFkJTIwYm93bHxlbnwxfHx8fDE3Njg1MDI4ODJ8MA&ixlib=rb-4.1.0&q=80&w=1080",
        prepTime = 15,
        servings = 4,
        ingredients = listOf(
            "200g de lechuga mixta",
            "2 tomates medianos",
            "1 pepino",
            "1 pimiento rojo",
            "1 aguacate",
            "100g de queso feta",
            "Nueces y vinagreta"
        ),
        steps = listOf(
            Step("Lava y seca bien toda la lechuga."),
            Step("Corta los tomates, pepino y pimiento."),
            Step("Corta el aguacate en cubos."),
            Step("Tuesta las nueces ligeramente.", 3),
            Step("Prepara la vinagreta."),
            Step("Mezcla todo justo antes de servir.")
        )
    )
)
