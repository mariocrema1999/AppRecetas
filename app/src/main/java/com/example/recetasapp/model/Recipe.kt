package com.example.recetasapp.model

import android.os.Parcelable
import com.example.recetasapp.R
import kotlinx.parcelize.Parcelize

enum class RecipeCategory(val displayName: String) {
    CARNE("Carne"),
    PESCADO("Pescado"),
    ARROCES("Arroces"),
    VERDURAS_HORTALIZAS("Verduras y Hortalizas"),
    LEGUMBRES("Legumbres"),
    HUEVOS("Huevos"),
    POSTRES("Postres"),
    PASTAS("Pastas"),
    SOPAS_CREMAS("Sopas y Cremas"),
    CELIACOS("Para Celíacos"),
    DIABETICOS("Para Diabéticos")
}

enum class Allergen(val displayName: String, val iconResId: Int) {
    GLUTEN("Cereales con gluten", R.drawable.allergengluten),
    CRUSTACEOS("Crustáceos", R.drawable.crustaceos),
    MOLUSCOS("Moluscos", R.drawable.moluscos),
    PESCADO("Pescado", R.drawable.pescado),
    HUEVOS("Huevos", R.drawable.huevos),
    ALTRAMUCES("Altramuces", R.drawable.altramuces),
    MOSTAZA("Mostaza", R.drawable.mostaza),
    CACAHUETES("Cacahuetes", R.drawable.cacahuetes),
    FRUTOS_CASCARA("Frutos de cáscara", R.drawable.frutosecos),
    SOJA("Soja", R.drawable.soja),
    SESAMO("Sésamo", R.drawable.sesamo),
    APIO("Apio", R.drawable.apio),
    LACTEOS("Leche y lácteos", R.drawable.lacteos),
    SULFITOS("Sulfitos", R.drawable.alergenossulfitos)
}

@Parcelize
data class Step(
    val description: String,
    val timeMinutes: Int? = null
) : Parcelable

@Parcelize
data class RecipeIngredient(
    val name: String,
    val quantity: String? = null
) : Parcelable

@Parcelize
data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val prepTime: Int,
    val servings: Int,
    val ingredients: List<RecipeIngredient>,
    val steps: List<Step>,
    val categories: List<RecipeCategory> = emptyList(),
    val allergens: List<Allergen>? = emptyList()
) : Parcelable

// TODO cuando hagamos la base de datos las recetas tendran un atributo nombre de usuario para que al clickar en mis recetas solo se muestren las recetas creadas por el usuario logeado.


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
            RecipeIngredient("Pasta (espagueti o linguini)", "400g"),
            RecipeIngredient("Panceta o tocino en cubos", "200g"),
            RecipeIngredient("Huevos grandes", "4"),
            RecipeIngredient("Queso parmesano rallado", "100g"),
            RecipeIngredient("Pimienta negra recién molida"),
            RecipeIngredient("Sal al gusto")
        ),
        steps = listOf(
            Step("Pon a hervir agua con sal en una olla grande y cocina la pasta según las instrucciones del paquete hasta que esté al dente.", 10),
            Step("Mientras tanto, en un bol grande, bate los huevos con el queso parmesano rallado y una generosa cantidad de pimiento negra."),
            Step("En una sartén grande, cocina la panceta a fuego medio hasta que esté dorada y crujiente.", 7),
            Step("Cuando la pasta esté lista, reserva 1 taza del agua de cocción y escurre la pasta."),
            Step("Retira la sartén del fuego y añade la pasta caliente a la panceta. Mezcla bien.", 2),
            Step("Añade la mezcla de huevo y queso a la pasta caliente, mezclando rápidamente."),
            Step("Si la salsa está muy espesa, añade poco a poco el agua de cocción reservada."),
            Step("Sirve inmediatamente con más queso parmesano y pimiento negra por encima.")
        ),
        categories = listOf(RecipeCategory.PASTAS, RecipeCategory.HUEVOS, RecipeCategory.CARNE),
        allergens = listOf(Allergen.GLUTEN, Allergen.HUEVOS, Allergen.LACTEOS)
    ),
    Recipe(
        id = "2",
        name = "Tarta de Chocolate",
        description = "Delicioso postre de chocolate intenso con textura húmeda y esponjosa",
        image = "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjaG9jb2xhdGUlMjBjYWtlfGVufDF8fHx8MTc2ODU3MDQ1MHww&ixlib=rb-4.1.0&q=80&w=1080",
        prepTime = 50,
        servings = 8,
        ingredients = listOf(
            RecipeIngredient("Chocolate negro", "200g"),
            RecipeIngredient("Mantequilla", "150g"),
            RecipeIngredient("Huevos", "4"),
            RecipeIngredient("Azúcar", "200g"),
            RecipeIngredient("Harina", "100g"),
            RecipeIngredient("Extracto de vainilla", "1 cucharadita"),
            RecipeIngredient("Sal", "Una pizca")
        ),
        steps = listOf(
            Step("Precalienta el horno a 180°C y engrasa un molde redondo de 22cm.", 10),
            Step("Derrite el chocolate con la mantequilla al baño maría.", 5),
            Step("En un bol grande, bate los huevos con el azúcar hasta que la mezcla esté espumosa.", 5),
            Step("Añade el chocolate derretido a la mezcla de huevos y bate suavemente."),
            Step("Incorpora la harina tamizada, la vainilla y la sal."),
            Step("Vierte la mezcla en el molde y hornea.", 35),
            Step("Deja enfriar en el molde antes de desmoldar.", 10)
        ),
        categories = listOf(RecipeCategory.HUEVOS, RecipeCategory.POSTRES),
        allergens = listOf(Allergen.HUEVOS, Allergen.LACTEOS, Allergen.GLUTEN)
    ),
    Recipe(
        id = "3",
        name = "Ensalada Fresca de Verano",
        description = "Ensalada nutritiva y colorida con vegetales frescos",
        image = "https://images.unsplash.com/photo-1620019989479-d52fcedd99fe?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMHNhbGFkJTIwYm93bHxlbnwxfHx8fDE3Njg1MDI4ODJ8MA&ixlib=rb-4.1.0&q=80&w=1080",
        prepTime = 15,
        servings = 4,
        ingredients = listOf(
            RecipeIngredient("Lechuga mixta", "200g"),
            RecipeIngredient("Tomates medianos", "2"),
            RecipeIngredient("Pepino", "1"),
            RecipeIngredient("Pimiento rojo", "1"),
            RecipeIngredient("Aguacate", "1"),
            RecipeIngredient("Queso feta", "100g"),
            RecipeIngredient("Nueces"),
            RecipeIngredient("Vinagreta")
        ),
        steps = listOf(
            Step("Lava y seca bien toda la lechuga."),
            Step("Corta los tomates, pepino y pimiento."),
            Step("Corta el aguacate en cubos."),
            Step("Tuesta las nueces ligeramente.", 3),
            Step("Prepara la vinagreta."),
            Step("Mezcla todo justo antes de servir.")
        ),
        categories = listOf(RecipeCategory.VERDURAS_HORTALIZAS),
        allergens = listOf(Allergen.LACTEOS, Allergen.FRUTOS_CASCARA)
    ),
    Recipe(
        id = "4",
        name = "Dorada al horno",
        description = "Dorada fresca, saludable y sabrosa",
        image = "https://static.bainet.es/clip/72de3c44-fb88-46c5-a70b-d9ae43d30d79_source-aspect-ratio_1600w_0.jpg",
        prepTime = 13,
        servings = 2,
        ingredients = listOf(
            RecipeIngredient("Doradas", "2"),
            RecipeIngredient("Tomates", "2"),
            RecipeIngredient("Dientes de ajo", "5"),
            RecipeIngredient("Guindilla", "1"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Sal gruesa"),
            RecipeIngredient("Perejil picado")
        ),
        steps = listOf(
            Step("Limpia las doradas y saca los lomos, sin retirar la piel."),
            Step("Cubre la placa del horno con papel de aluminio. Vierte un chorrito de aceite y coloca los lomos dejando la piel hacia arriba. Añade sal gruesa. Introduce al horno a 220º durante 10 minutos",10),
            Step("Cuando estén hechos retira la piel con cuidado y colocalos en una fuente amplia."),
            Step("Limpia unos tomates y cortalos en rodajas gruesas y frielas en una sartén con un poco de aceite unos 3 minutos por cada lado a fuego medio-alto."),
            Step("Colocalas en la misma fuente que el pescado."),
            Step("Pon 8 cucharadas de aceite en otra sartén, calienta y añade los dientes de ajo cortados en láminas, cocinalos durante 2 minutos a fuego medio-bajo. Cuando se doren un poco añade unas tiras de guindilla y cocinalas otro minuto a fuego medio-bajo.",3),
            Step("Vierte el aceite sobre el pescado. Espolvorea con perejil picado.")
        ),
        categories = listOf(RecipeCategory.PESCADO, RecipeCategory.VERDURAS_HORTALIZAS),
        allergens = listOf(Allergen.PESCADO)
    ),
    Recipe(
        id = "5",
        name = "Crepes de habas",
        description = "Opción sana, elegante y de temporada",
        image = "https://menudiario.com/files/2012/02/019.gif",
        prepTime = 50,
        servings = 2,
        ingredients = listOf(
            RecipeIngredient("Leche", "400 ml"),
            RecipeIngredient("Harina", "200 g"),
            RecipeIngredient("Huevos", "2"),
            RecipeIngredient("Cebolletas", "2"),
            RecipeIngredient("Habas peladas", "1/2 kg"),
            RecipeIngredient("Diente de ajo", "1"),
            RecipeIngredient("Judías verdes", "100 g"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Pimiento verde", "1"),
            RecipeIngredient("Harina", "1 cucharada"),
            RecipeIngredient("Agua", "1 vaso"),
            RecipeIngredient("Sal")
        ),
        steps = listOf(
            Step("Desgrana las habas y ponlas a cocer en una cazuela con agua y una pizca de sal.",13),
            Step("Pon un poco de aceite in una sartén y pon a pochar a fuego bajo el ajo picado finamente, las judías y los pimientos cortados en juliana fina.", 20),
            Step("Cuando esté pochado, agrega una cucharada de harina."),
            Step("Rehoga brevemente y vierte el vino, el agua y una pizca de sal Deja cocer 10 minutos.", 10),
            Step("Pon en una jarra la leche, el rsto de la harina(reserva una cucharada, un poco de perejil picado, los huevos, una cucharada de aceite y bate con una batidora"),
            Step("Calienta bien la sarten, vierte un poco de masa y espárcela por el fondo, cuando empiece a hacer burbujas dale la vuelta. Continua asi hasta terminar toda la masa"),
            Step("Pica finalmente las cebolletas y ponlas a dorar in una sartén con aceite. Saltea hasta que se dore a fuego medio-alto", 7),
            Step("Agrega una cucharada de harina previamente reserbada y mezcla, incorpora las habas y saltea"),
            Step("Deja que se temple y rellena las creepes. Sirve con las verduras")
        ),
        categories = listOf(RecipeCategory.LEGUMBRES, RecipeCategory.VERDURAS_HORTALIZAS, RecipeCategory.HUEVOS),
        allergens = listOf(Allergen.LACTEOS, Allergen.GLUTEN, Allergen.HUEVOS)
    ),
    Recipe(
        id = "6",
        name = "Pastel de carne",
        description = "Familiar, económico y reconfortante",
        image = "https://cocinaconnoelia.com/wp-content/uploads/2023/05/Pastel-de-ternera-y-pure-de-patata-fuente-scaled.webp",
        prepTime = 22,
        servings = 3,
        ingredients = listOf(
            RecipeIngredient("Carne de ternera", "500 g"),
            RecipeIngredient("Carne de cerdo", "250 g"),
            RecipeIngredient("Patatas", "1 kg"),
            RecipeIngredient("Leche", "1 vaso"),
            RecipeIngredient("Cebolla", "1"),
            RecipeIngredient("Dientes de ajo", "2"),
            RecipeIngredient("Aceitunas", "18"),
            RecipeIngredient("Salsa de tomate", "1/2 litro"),
            RecipeIngredient("Queso rallado", "50 g"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Pimienta negra"),
            RecipeIngredient("Sal"),
            RecipeIngredient("Perejil")
        ),
        steps = listOf(
            Step("Pela las patatas, trocealas y ponlas a cocer durante 17 minutos en una cazuela con agua y sal.",17),
            Step("Trocea las carnes en daditos y salpiméntalas"),
            Step("Pica finamente la cebolla y los dientes de ajo, ponlos a dorar in una cazuela con un poco de aceite. Cuando se dore un poco añade la carne y las aceitunas picadas. Rehoga brevemente y vierte la salsa de tomate. Añadi pimienta. Mezcla todo y sirve en una fuente apta para horno"),
            Step("Cuando las patatas estén cocidas escúrrelas y pásalas por el pasapuré, vierte la lecha salpimenta, espolvorea con perejil y mezcla bien."),
            Step("Cubre la carne con el puré, espolvorea con el queso rallado."),
            Step("Gratina en el horno durante 5 minutos", 5)
        ),
        categories = listOf(RecipeCategory.CARNE),
        allergens = listOf(Allergen.LACTEOS)
    ),
    Recipe(
        id = "7",
        name = "Sopa de arroz y verduras ",
        description = "Plato de cuchara reconfortante, rápido y nutritivo",
        image = "https://imag.bonviveur.com/arroz-caldoso-de-verduras-casero.jpg",
        prepTime = 22,
        servings = 2,
        ingredients = listOf(
            RecipeIngredient("Arroz", "100 g"),
            RecipeIngredient("Cebolletas", "2"),
            RecipeIngredient("Puerro", "1"),
            RecipeIngredient("Tomate maduro", "1"),
            RecipeIngredient("Zanahoria", "1"),
            RecipeIngredient("Judías verdes", "150 g"),
            RecipeIngredient("Maíz cocido", "100 g"),
            RecipeIngredient("Caldo de verduras", "1,5 l"),
            RecipeIngredient("Huevos cocidos", "2"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Azafrán", "Unas hebras"),
            RecipeIngredient("Sal")
        ),
        steps = listOf(
            Step("Pica fina toda la verdura."),
            Step("Pon la verdura a pochar a fuego medio-bajo durante 17 minutos en una cazuela con un poco de aceite, sazona y agrega unas hebras de azafrán.",17),
            Step("Cuando tomen un poco de color añade el caldo y, al romper a hervir, añade el arroz y déjalo cocer durante 15 minutos.", 15),
            Step("Pela y pica los huevos e incorporalos."),
            Step("Sirve en una sopera.")
        ),
        categories = listOf(RecipeCategory.ARROCES, RecipeCategory.VERDURAS_HORTALIZAS, RecipeCategory.SOPAS_CREMAS, RecipeCategory.HUEVOS),
        allergens = listOf(Allergen.HUEVOS)
    ),
    Recipe(
        id = "8",
        name = "Conejo con alcachofas",
        description = "Guiso tradicional y sabroso",
        image = "https://static.bainet.es/clip/8fb410b9-79ed-4009-bfec-ab03dcdc2d36_source-aspect-ratio_1600w_0.jpg",
        prepTime = 25,
        servings = 3,
        ingredients = listOf(
            RecipeIngredient("Conejo", "1,200 kg"),
            RecipeIngredient("Alcachofas", "6"),
            RecipeIngredient("Dientes de ajo", "4"),
            RecipeIngredient("Cebolletas", "2"),
            RecipeIngredient("Pimientos verdes", "2"),
            RecipeIngredient("Harina", "1 cucharada"),
            RecipeIngredient("Vino blanco", "1 vaso"),
            RecipeIngredient("Agua", "2 vasos"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Pimienta"),
            RecipeIngredient("Sal")
        ),
        steps = listOf(
            Step("Trocea el conejo y salpimentalo."),
            Step("Pela los aojos y cortalos por la mitad"),
            Step("Dora los ajos in una sarten con poco aceite durante un minuto a fuego medio-alto.", 1),
            Step("Añade the conejo y doralo a fuego medio-alto durante 12 minutos.", 12),
            Step("Agrega una cucharada de harina y mezcla bien hasta que se disuelva."),
            Step("Limpia las alcachofas, retirando el tallo, las hojas externas y la parte alta de las hojas, trocea en cuatro e incorporalas a la cazuela."),
            Step("Vierte el vino y el agua a fuego medio-alto durante 30 minutos."),
            Step("Trocea los pimientos en rectangulos y las cebollas en tiras."),
            Step("Pon las verduras a dorar in una sarten a fuego medio unos 12 minutos.", 12),
            Step("Sirve el conejo en una fuente and coloca encima los pimientos y las cebolletas salteadas.")
        ),
        categories = listOf(RecipeCategory.CARNE, RecipeCategory.VERDURAS_HORTALIZAS),
        allergens = listOf(Allergen.GLUTEN, Allergen.SULFITOS)
    ),
    Recipe(
        id = "9",
        name = "Lomo de cerdo con champiñones",
        description = "Plato clásico, sencillo y sabroso",
        image = "https://static.bainet.es/clip/971694c5-bc19-48b7-9bcd-a942bac72b05_source-aspect-ratio_1600w_0.jpg",
        prepTime = 25,
        servings = 2,
        ingredients = listOf(
            RecipeIngredient("Lomo fresco", "600 g"),
            RecipeIngredient("Champiñón en conserva", "400 g"),
            RecipeIngredient("Cebollas", "2"),
            RecipeIngredient("Leche", "1 vaso"),
            RecipeIngredient("Huevos", "2"),
            RecipeIngredient("Harina"),
            RecipeIngredient("Perejil"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Pimienta"),
            RecipeIngredient("Sal")
        ),
        steps = listOf(
            Step("Pica finamente las cebollas y ponlas a dorar in una cazuela con un poco de aceite a fuego medio durante 12 minutos.", 12),
            Step("Cuando esté bien dorada vierte la leche y los champiñones y deja reducir"),
            Step("Mientras tanto, filetea el lomo y salpimenta, pasa los filetes por harina y huevo batido."),
            Step("Frie el lomo brevemente (vuelta y vuelta) in una sartén con aceite. Escurrelos sobre un papel de cocina.", 12),
            Step("Sirve el lomo acompañado de los champiñones y espolvorea con perejil picado.")
        ),
        categories = listOf(RecipeCategory.CARNE, RecipeCategory.HUEVOS),
        allergens = listOf(Allergen.HUEVOS, Allergen.LACTEOS, Allergen.GLUTEN)
    ),
    Recipe(
        id = "10",
        name = "Tortilla de patatas",
        description = "Jugoso y clásico plato español",
        image = "https://www.miscosillasdecocina.com/wp-content/uploads/2019/02/tortilla-patata-pimientos.jpg",
        prepTime = 13,
        servings = 4,
        ingredients = listOf(
            RecipeIngredient("Patatas", "1/2 kg"),
            RecipeIngredient("Cebollas", "1"),
            RecipeIngredient("Pimiento verde", "1/2"),
            RecipeIngredient("Huevos", "4"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Sal")
        ),
        steps = listOf(
            Step("Pela las cebollas y las patatas."),
            Step("Pica las cebollas en trozos no muy pequeños y ponlas a dorar duarnte 15 minutos a fuego medio-bajo in una sartén con abundante aceite."),
            Step("Mientras tanto, pica las patatas en dados, sazonalas y añadelas a la sartén. Agrega el pimiento picado en dados y frie todo a fuego medio, removiendo de vez en cuando, hasta que se dore todo un poco."),
            Step("Retiralas y escurrelas."),
            Step("Prepara un recipiente y bate 4 huevos."),
            Step("Agrega a cada uno la mitad de las patatas, cebollas y pimiento."),
            Step("Pon un poco de aceite in una sartén y vierte la mezcla anterior. Cuaja el huevo, primero a fuego vivo y despues un poco más suave. Voltea la tortilla para que se haga por los dos lados.")
        ),
        categories = listOf(RecipeCategory.HUEVOS, RecipeCategory.VERDURAS_HORTALIZAS)
    ),
    Recipe(
        id = "11",
        name = "Arroz en salsa verde",
        description = "Plato tradicional, reconfortante y sencillo",
        image = "https://static.bainet.es/clip/fd0fe304-0c5d-467a-888c-f5540b0aff6a_source-aspect-ratio_1600w_0.jpg",
        prepTime = 34,
        servings = 4,
        ingredients = listOf(
            RecipeIngredient("Arroz", "200 g"),
            RecipeIngredient("Cebolletas", "2"),
            RecipeIngredient("Ajos frescos", "10"),
            RecipeIngredient("Patatas", "3"),
            RecipeIngredient("Vino blanco", "2 vasos"),
            RecipeIngredient("Caldo", "2-3 vasos"),
            RecipeIngredient("Huevo cocido", "1"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Sal"),
            RecipeIngredient("Perejil")
        ),
        steps = listOf(
            Step("Pela las patatas, cortalas en rodajas y frielas a fuego fuerte in una sartén hasta que se doren durante 12 minutos a fuego medio.", 12),
            Step("Pica las cebolletas y corta los ajos frescos en trozos de unos 5 centimetros. Dóralos in una cazuela amplia y baja con 4 cucharadas de aceite unos 5 minutos a fuego medio-alto.", 5),
            Step("Incorporalas patatas y rehoga."),
            Step("Cuando esté rehogado añade el arroz, mezcla bien y añade el vino blanco y el caldo. Sazona y espolvorea perejil picado. Deja cocer durante 17 minutos.", 17),
            Step("Pela el huevo cocido y córtalo en 4."),
            Step("Decora la cazuela con los trozos de huevo y una rama de perejil.")
        ),
        categories = listOf(RecipeCategory.ARROCES, RecipeCategory.HUEVOS),
        allergens = listOf(Allergen.HUEVOS, Allergen.SULFITOS)
    ),
    Recipe(
        id = "12",
        name = "Tallarines con oreja de cerdo",
        description = "Un plato crujiente y meloso",
        image = "https://static.bainet.es/clip/8722edb4-31e8-4c24-b0ed-ba1515a9530b_source-aspect-ratio_1600w_0.jpg",
        prepTime = 13,
        servings = 4,
        ingredients = listOf(
            RecipeIngredient("Orejas de cerdo", "2"),
            RecipeIngredient("Cebolletas", "3"),
            RecipeIngredient("Cabeza de ajos", "1"),
            RecipeIngredient("Dientes de ajo", "2"),
            RecipeIngredient("Tallarines", "250 g"),
            RecipeIngredient("Pimiento morrón", "1"),
            RecipeIngredient("Pimientos verdes", "2"),
            RecipeIngredient("Pimentón", "2 cucharaditas"),
            RecipeIngredient("Aceite de oliva virgen extra"),
            RecipeIngredient("Agua y sal"),
            RecipeIngredient("Salsa de tomate", "1/2 litro")
        ),
        steps = listOf(
            Step("Pon una cazuela al fuego con abundante agua, un chorro de aceite y una pizca de sal."),
            Step("Cuando empiece a hervir, añade los tallarines y cuécelos durante 10 minutos.",10),
            Step("Escurre y refrescalos."),
            Step("Pon agua in una hoya a presión, agrega 1 cebolleta, la cabeza de ajos, una pizca de sal y las orejas del cerdo. Tapa la hoya y deja cocer durante 10 minutos desde que empiece  salir el vapor", 10),
            Step("Retira las orejas, deja que se templen y trocealas."),
            Step("Para la salsa, pica las cebolletas en juliana fina. Pela los ajos y córtalos en láminas."),
            Step("Pon todo a pochar in una sartén con aceite."),
            Step("Cuando esté bien pochado, añade un par de cucharaditas de pimentón, mezcla bien, agrega la salsa de tomate y los trozos de oreja de cerdo."),
            Step("Añade la pasta a la sartén, mezcla y sirve en una fuente amplia."),
        ),
        categories = listOf(RecipeCategory.PASTAS, RecipeCategory.CARNE),
        allergens = listOf(Allergen.GLUTEN)
    ),
    Recipe(
        id = "13",
        name = "Tarta de queso",
        description = "Sabrosa tarta de queso para diabéticos",
        image = "https://vod-hogarmania.atresmedia.com/hogarmania/images/images01/2013/06/19/5c0018f416334d00019191d3/1239x697.jpg",
        prepTime = 40,
        servings = 8,
        ingredients = listOf(
            RecipeIngredient("Queso de untar", "400 g"),
            RecipeIngredient("Galletas(especiales diabéticos)", "200 g"),
            RecipeIngredient("Mantequilla a punto de pomada", "150 g"),
            RecipeIngredient("Nata líquida", "300 mg"),
            RecipeIngredient("Sirópe de ágave", "1 cucharadita"),
            RecipeIngredient("Hojas de gelatina", "6"),
            RecipeIngredient("Agua"),
            RecipeIngredient("Mermelada de frambuesa"),
            RecipeIngredient("Fresas", "4-6"),
            RecipeIngredient("Hojas de menta")
        ),
        steps = listOf(
            Step("Tritura las galletas y colócalas en un bol. Añade la mantequilla y mezcla bien. Cubre el fondo del molde e introdúcelo en el frigorífico para que la masa endurezca un poco."),
            Step("Pon la gelatina a remojo in un bol con agua fría hasta que se ablande."),
            Step("Calienta la mitad de la nata, introduce las hojas de gelatina escurridas y espera a que se disuelvan. Agrega el resto de la nata, el queso, la cucharadita de sirope de ágave y mezcla bien con una varilla de mano."),
            Step("Vierte la mezcla sobre la base de galletas e introduce la tarta en el frigorífico hasta que endurezca (40 minutos aproximadamente).", 40),
            Step("Calienta la mermelada para que se ablande un poco, cuélala y cubre la superficie de la tarta. Desmolda y decórala con unas fresas y unas hojas de menta."),
            Step("Para la salsa, pica las cebolletas en juliana fina. Pela los ajos y córtalos en láminas.")
        ),
        categories = listOf(RecipeCategory.DIABETICOS),
        allergens = listOf(Allergen.LACTEOS, Allergen.GLUTEN)
    ),
    Recipe(
        id = "14",
        name = "Pan casero de arroz",
        description = "Pan casero de arroz esponjoso y crujiente para celíacos",
        image = "https://fotografias.antena3.com/clipping/cmsimages01/2023/01/25/F3648755-289A-49A4-96E6-C7BDCE279CBA/arguinano-receta-pan-casero-arroz-esponjoso-perfecto-celiacos_70.jpg?crop=1200,675,x0,y64&width=480&height=270&optimize=high&format=webply",
        prepTime = 148,
        servings = 4,
        ingredients = listOf(
            RecipeIngredient("Arroz glutinoso (tipo sushi)", "230 g"),
            RecipeIngredient("Aceite de oliva virgen extra", "25 ml"),
            RecipeIngredient("Sirope de arce", "15 ml"),
            RecipeIngredient("Sal", "5 g"),
            RecipeIngredient("Levadura seca en polvo", "4 g"),
            RecipeIngredient("Agua", "150 ml"),
            RecipeIngredient("Romero, tomillo y menta (para decorar)")
        ),
        steps = listOf(
            Step("Pon el arroz in un bol, cúbrelo con agua y ponlo a remojo desde la víspera."),
            Step("Escúrrelo y colócalo in un vaso americano. Añade el aceite, el sirope de arce, la sal y la levadura."),
            Step("Calienta el agua y viértela encima."),
            Step("Tritura los ingredientes hasta conseguir un puré homogéneo y cuela la mezcla."),
            Step("Para para que luego sea más fácil desmoldar el pan, cubre un molde de papel de aluminio con papel sulfurizado (de horno)."),
            Step("Vierte la mezcla in el molde y deja que fermente in el horno a 40ºC durante 2 horas y media hasta que doble su tamaño. A la hora y cuarto, pulverízalo con un poco de agua.",120),
            Step("Sube la temperatura del horno a 180º, pulveriza el pan con otro poco de agua y hornéalo durante 25-30 minutos.", 28),
            Step("Deja que se enfríe y sirve. Adorna los platos con unas ramas de tomillo, de romero y unas hojas de menta.")
        ),
        categories = listOf(RecipeCategory.CELIACOS),
        allergens = emptyList()
    ),
)
