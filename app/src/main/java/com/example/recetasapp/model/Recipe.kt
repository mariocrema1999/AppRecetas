package com.example.recetasapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
//añadir temporada: verano/invierno y bebida para acompañar: vino/cava...
//consejos
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
    ),
    Recipe(
        id = "4",
        name = "Dorada al horno",
        description = "Dorada fresca, saludable y sabrosa",
        image = "https://static.bainet.es/clip/72de3c44-fb88-46c5-a70b-d9ae43d30d79_source-aspect-ratio_1600w_0.jpg",
        prepTime = 13,
        servings = 2,
        ingredients = listOf(
            "2 doradas",
            "2 tomates",
            "5 dientes de ajo",
            "1 guindilla",
            "Aceite de oliva virgen extra",
            "sal gruesa",
            "perejil picado"
        ),
        steps = listOf(
            Step("Limpia las doradas y saca los lomos, sin retirar la piel."),
            Step("Cubre la placa del horno con papel de aluminio. Vierte un chorrito de aceite y coloca los lomos dejando la piel hacia arriba. Añade sal gruesa. Introduce al horno a 220º durante 10 minutos",10),
            Step("Cuando estén hechos retira la piel con cuidado y colocalos en una fuente amplia."),
            Step("Limpia unos tomates y cortalos en rodajas gruesas y frielas en una sartén con un poco de aceite unos 3 minutos por cada lado a fuego medio-alto."),
            Step("Colocalas en la misma fuente que el pescado."),
            Step("Pon 8 cucharadas de aceite en otra sartén, calienta y añade los dientes de ajo cortados en láminas, cocinalos durante 2 minutos a fuego medio-bajo. Cuando se doren un poco añade unas tiras de guindilla y cocinalas otro minuto a fuego medio-bajo.",3),
            Step("Vierte el aceite sobre el pescado. Espolvorea con perejil picado.")
        )
        //verano, vino rosado
    ),
    Recipe(
        id = "5",
        name = "Crepes de habas",
        description = "Opción sana, elegante y de temporada",
        image = "https://menudiario.com/files/2012/02/019.gif",
        prepTime = 50,
        servings = 2,
        ingredients = listOf(
            "400 ml de leche",
            "200 g de harina",
            "2 huevos",
            "2 cebolletas",
            "1/2 kg de habas peladas",
            "1 diente de ajo",
            "100 g de judías verdes",
            "Aceite de oliva virgen extra",
            "1 pimiento verde",
            "1 cucharada de harina",
            "1 vaso de agua",
            "Sal"
        ),
        steps = listOf(
            Step("Desgrana las habas y ponlas a cocer en una cazuela con agua y una pizca de sal.",13),
            Step("Pon un poco de aceite en una sartén y pon a pochar a fuego bajo el ajo picado finamente, las judías y los pimientos cortados en juliana fina.", 20),
            Step("Cuando esté pochado, agrega una cucharada de harina."),
            Step("Rehoga brevemente y vierte el vino, el agua y una pizca de sal Deja cocer 10 minutos.", 10),
            Step("Pon en una jarra la leche, el rsto de la harina(reserva una cucharada, un poco de perejil picado, los huevos, una cucharada de aceite y bate con una batidora"),
            Step("Calienta bien la sarten, vierte un poco de masa y espárcela por el fondo, cuando empiece a hacer burbujas dale la vuelta. Continua asi hasta terminar toda la masa"),
            Step("Pica finalmente las cebolletas y ponlas a dorar en una sarten con aceite. Saltea hasta que se dore a fuego medio-alto", 7),
            Step("Agrega una cucharada de harina previamente reserbada y mezcla, incorpora las habas y saltea"),
            Step("Deja que se temple y rellena las creepes. Sirve con las verduras")
        )
        //temporada primavera bebida sidra
    ),
    Recipe(
        id = "6",
        name = "Pastel de carne",
        description = "Familiar, económico y reconfortante",
        image = "https://cocinaconnoelia.com/wp-content/uploads/2023/05/Pastel-de-ternera-y-pure-de-patata-fuente-scaled.webp",
        prepTime = 13,
        servings = 3,
        ingredients = listOf(
            "500 g de carne de ternera",
            "250 g de carne de cerdo",
            "1 kg de patatas",
            "1 vaso de leche",
            "1 cebolla",
            "2 dientes de ajo",
            "18 aceitunas",
            "1/2 litro de salsa de tomate",
            "50 g de queso rallado",
            "Aceite de oliva virgen extra",
            "Pimienta negra",
            "Sal",
            "Perejil"
        ),
        steps = listOf(
            Step("Pela las patatas, trocealas y ponlas a cocer durante 17 minutos en una cazuela con agua y sal.",17),
            Step("Trocea las carnes en daditos y salpiméntalas"),
            Step("Pica finamente la cebolla y los dientes de ajo, ponlos a dorar en una cazuela con un poco de aceite. Cuando se dore un poco añade la carne y las aceitunas picadas. Rehoga brevemente y vierte la salsa de tomate. Añadi pimienta. Mezcla todo y sirve en una fuente apta para horno"),
            Step("Cuando las patatas estén cocidas escúrrelas y pásalas por el pasapuré, vierte la lecha salpimenta, espolvorea con perejil y mezcla bien."),
            Step("Cubre la carne con el puré, espolvorea con el queso rallado."),
            Step("Gratina en el horno durante 5 minutos", 5)
        )
    //verano, tinto del año
    ),
    Recipe(
        id = "7",
        name = "Sopa de arroz y verduras ",
        description = "Plato de cuchara reconfortante, rápido y nutritivo",
        image = "https://imag.bonviveur.com/arroz-caldoso-de-verduras-casero.jpg",
        prepTime = 13,
        servings = 3,
        ingredients = listOf(
            "100 g de arroz",
            "2 cebolletas",
            "1 puerro",
            "1 tomate maduro",
            "1 zanahoria",
            "150 g de judías verdes",
            "100 g de maíz cocido",
            "1,5 l de caldo de verduras",
            "2 huevos cocidos",
            "Aceite de oliva virgen extra",
            "Unas hebras de azafrán",
            "Sal"
        ),
        steps = listOf(
            Step("Pica fina toda la verdura."),
            Step("Pon la verdura a pochar a fuego medio-bajo durante 17 minutos en una cazuela con un poco de aceite, sazona y agrega unas hebras de azafrán.",17),
            Step("Cuando tomen un poco de color añade el caldo y, al romper a hervir, añade el arroz y déjalo cocer durante 15 minutos.", 15),
            Step("Pela y pica los huevos e incorporalos."),
            Step("Sirve en una sopera.")
        )
        //Otoño, tinto del año
    )
)
