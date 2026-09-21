package com.studiolexair.preguntascalientes.utils

import com.studiolexair.preguntascalientes.models.Category
import com.studiolexair.preguntascalientes.models.Question

/**
 * Base de datos local de preguntas - 120 preguntas totales
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
object QuestionDatabase {

    fun getAllQuestions(): List<Question> = mutableListOf<Question>().apply {
        // ==================== 🔥 CALIENTES (Con Pareja) - 10 ====================
        add(Question(1, "¿Cuál es el lugar más atrevido donde has besado a tu pareja?", Category.CALIENTES, true, 2))
        add(Question(2, "¿Tu pareja sabe tu mayor fantasía?", Category.CALIENTES, true, 3))
        add(Question(3, "¿Has pensado en alguien más estando con tu pareja?", Category.CALIENTES, true, 3))
        add(Question(4, "¿Cuál es el mensaje más hot que le has enviado a tu pareja?", Category.CALIENTES, true, 2))
        add(Question(5, "¿Tu pareja es celosa? ¿Te gusta que lo sea?", Category.CALIENTES, true, 1))
        add(Question(6, "¿Qué es lo más loco que has hecho por amor?", Category.CALIENTES, true, 2))
        add(Question(7, "¿Te has arrepentido de estar con tu pareja actual?", Category.CALIENTES, true, 3))
        add(Question(8, "¿Con qué frecuencia discuten por celos?", Category.CALIENTES, true, 1))
        add(Question(9, "¿Has ocultado algo importante a tu pareja?", Category.CALIENTES, true, 2))
        add(Question(10, "¿Cuál es tu recuerdo más apasionado con tu pareja?", Category.CALIENTES, true, 3))

        // ==================== 🔥 CALIENTES (Soltero) - 10 ====================
        add(Question(11, "¿A quién del grupo besarías ahora mismo?", Category.CALIENTES, false, 2))
        add(Question(12, "¿Cuál fue tu última aventura de una noche?", Category.CALIENTES, false, 3))
        add(Question(13, "¿Con quién de aquí tendrías una cita?", Category.CALIENTES, false, 1))
        add(Question(14, "¿Cuál es tu mayor fantasía inconfesable?", Category.CALIENTES, false, 3))
        add(Question(15, "¿Has mandado nudes? ¿A quién?", Category.CALIENTES, false, 3))
        add(Question(16, "¿Cuál es tu tipo físico ideal? Señala a alguien que se acerque", Category.CALIENTES, false, 2))
        add(Question(17, "¿Qué es lo más atrevido que has hecho estando soltero?", Category.CALIENTES, false, 2))
        add(Question(18, "¿Te has enamorado de un amigo/a?", Category.CALIENTES, false, 1))
        add(Question(19, "¿Con cuántas personas has estado este año?", Category.CALIENTES, false, 2))
        add(Question(20, "¿Qué harías si la persona que te gusta te escribe ahora mismo?", Category.CALIENTES, false, 1))

        // ==================== 🤔 INTERESANTES (Con Pareja) - 10 ====================
        add(Question(21, "¿Qué es lo que más admiras de tu pareja?", Category.INTERESANTES, true, 1))
        add(Question(22, "¿Cuál fue el momento en que supiste que era especial?", Category.INTERESANTES, true, 1))
        add(Question(23, "¿Qué cambiarías de tu relación?", Category.INTERESANTES, true, 2))
        add(Question(24, "¿Crees que tu pareja es el amor de tu vida?", Category.INTERESANTES, true, 1))
        add(Question(25, "¿Qué has aprendido estando con tu pareja?", Category.INTERESANTES, true, 1))
        add(Question(26, "¿Perdonarías una infidelidad?", Category.INTERESANTES, true, 2))
        add(Question(27, "¿Dónde te ves con tu pareja en 5 años?", Category.INTERESANTES, true, 1))
        add(Question(28, "¿Qué es lo más difícil de tener pareja?", Category.INTERESANTES, true, 1))
        add(Question(29, "¿Tu familia aprueba a tu pareja?", Category.INTERESANTES, true, 2))
        add(Question(30, "¿Qué secreto nunca le contarías a tu pareja?", Category.INTERESANTES, true, 3))

        // ==================== 🤔 INTERESANTES (Soltero) - 10 ====================
        add(Question(31, "¿Cuál es tu tipo ideal de persona?", Category.INTERESANTES, false, 1))
        add(Question(32, "¿Por qué terminó tu última relación?", Category.INTERESANTES, false, 2))
        add(Question(33, "¿Crees en el amor a primera vista?", Category.INTERESANTES, false, 1))
        add(Question(34, "¿Qué buscas en una relación?", Category.INTERESANTES, false, 1))
        add(Question(35, "¿Estás soltero por elección o por circunstancias?", Category.INTERESANTES, false, 2))
        add(Question(36, "¿Cuál es tu mayor miedo respecto al amor?", Category.INTERESANTES, false, 1))
        add(Question(37, "¿Te arrepientes de haber terminado con alguien?", Category.INTERESANTES, false, 2))
        add(Question(38, "¿Qué es lo que más valoras en una persona?", Category.INTERESANTES, false, 1))
        add(Question(39, "¿Crees que algún ex volverá a tu vida?", Category.INTERESANTES, false, 2))
        add(Question(40, "¿Qué harías si tu crush te rechaza?", Category.INTERESANTES, false, 1))

        // ==================== 😄 DIVERTIDAS (Universal) - 20 ====================
        add(Question(41, "¿Cuál es tu peor cita de la historia?", Category.DIVERTIDAS, null, 1))
        add(Question(42, "¿Qué mentira has dicho en una primera cita?", Category.DIVERTIDAS, null, 1))
        add(Question(43, "¿Cuál es tu mensaje de texto más vergonzoso?", Category.DIVERTIDAS, null, 2))
        add(Question(44, "¿Has stalkeado a tu ex en redes? ¿Qué encontraste?", Category.DIVERTIDAS, null, 1))
        add(Question(45, "¿Cuál es la excusa más tonta que has puesto para no salir?", Category.DIVERTIDAS, null, 1))
        add(Question(46, "¿Qué apodo vergonzoso te tiene tu familia?", Category.DIVERTIDAS, null, 1))
        add(Question(47, "¿Cuál fue tu momento más incómodo en una cita?", Category.DIVERTIDAS, null, 1))
        add(Question(48, "¿Has enviado un mensaje a la persona equivocada? ¿Qué decía?", Category.DIVERTIDAS, null, 2))
        add(Question(49, "¿Cuál es tu guilty pleasure musical?", Category.DIVERTIDAS, null, 1))
        add(Question(50, "¿Qué es lo más ridículo que has hecho por un crush?", Category.DIVERTIDAS, null, 1))
        add(Question(51, "¿Te han dejado en visto y qué hiciste después?", Category.DIVERTIDAS, null, 1))
        add(Question(52, "¿Cuál es tu peor experiencia con suegros?", Category.DIVERTIDAS, null, 2))
        add(Question(53, "¿Has llorado por alguien que no valía la pena?", Category.DIVERTIDAS, null, 1))
        add(Question(54, "¿Qué filtro usas para ligar en fotos?", Category.DIVERTIDAS, null, 1))
        add(Question(55, "¿Cuál es tu frase para ligar más mala?", Category.DIVERTIDAS, null, 1))
        add(Question(56, "¿Has sido rechazado de forma épica? Cuenta", Category.DIVERTIDAS, null, 1))
        add(Question(57, "¿Qué harías si ves a tu ex con alguien nuevo?", Category.DIVERTIDAS, null, 2))
        add(Question(58, "¿Cuál es tu talento oculto para conquistar?", Category.DIVERTIDAS, null, 1))
        add(Question(59, "¿Has usado Tinder/Bumble? ¿Peor experiencia?", Category.DIVERTIDAS, null, 2))
        add(Question(60, "¿Qué es lo más cursi que has hecho?", Category.DIVERTIDAS, null, 1))

        // ==================== 🌶️ ATREVIDAS (Con Pareja) - 10 ====================
        add(Question(61, "¿Has mandado fotos comprometedoras a tu pareja?", Category.ATREVIDAS, true, 2))
        add(Question(62, "¿Cuál es tu momento más apasionado con tu pareja?", Category.ATREVIDAS, true, 3))
        add(Question(63, "¿Tu pareja sabe todas tus contraseñas?", Category.ATREVIDAS, true, 2))
        add(Question(64, "¿Has revisado el celular de tu pareja?", Category.ATREVIDAS, true, 2))
        add(Question(65, "¿Qué fantasía te gustaría cumplir con tu pareja?", Category.ATREVIDAS, true, 3))
        add(Question(66, "¿Has tenido sueños con otra persona?", Category.ATREVIDAS, true, 3))
        add(Question(67, "¿Qué parte del cuerpo te gusta más de tu pareja?", Category.ATREVIDAS, true, 2))
        add(Question(68, "¿Has fingido alguna vez?", Category.ATREVIDAS, true, 3))
        add(Question(69, "¿Cuál es el lugar más raro donde lo han hecho?", Category.ATREVIDAS, true, 3))
        add(Question(70, "¿Te atreverías a tener una relación abierta?", Category.ATREVIDAS, true, 3))

        // ==================== 🌶️ ATREVIDAS (Soltero) - 10 ====================
        add(Question(71, "¿Has estado enamorado de alguien de aquí?", Category.ATREVIDAS, false, 2))
        add(Question(72, "¿Has tenido un crush con alguien comprometido?", Category.ATREVIDAS, false, 3))
        add(Question(73, "¿Cuál es tu secreto más guardado?", Category.ATREVIDAS, false, 3))
        add(Question(74, "¿Con quién de tus ex volverías una noche?", Category.ATREVIDAS, false, 2))
        add(Question(75, "¿Qué es lo más atrevido que te han pedido?", Category.ATREVIDAS, false, 3))
        add(Question(76, "¿Has besado a más de una persona en un día?", Category.ATREVIDAS, false, 2))
        add(Question(77, "¿Cuál es tu lugar favorito para besar?", Category.ATREVIDAS, false, 1))
        add(Question(78, "¿Has mentido sobre con cuántas personas has estado?", Category.ATREVIDAS, false, 2))
        add(Question(79, "¿Qué harías si tu mejor amigo/a te pide un beso?", Category.ATREVIDAS, false, 2))
        add(Question(80, "¿Te has arrepentido al día siguiente de algo que hiciste?", Category.ATREVIDAS, false, 2))

        // ==================== 💑 ROMÁNTICAS (Con Pareja) - 10 ====================
        add(Question(81, "¿Cuál fue tu primera cita con tu pareja?", Category.ROMANTICAS, true, 1))
        add(Question(82, "¿Qué es lo más bonito que te ha dicho tu pareja?", Category.ROMANTICAS, true, 1))
        add(Question(83, "¿Planeas un futuro juntos?", Category.ROMANTICAS, true, 1))
        add(Question(84, "¿Cuál es tu canción como pareja?", Category.ROMANTICAS, true, 1))
        add(Question(85, "¿Qué te enamoró de tu pareja?", Category.ROMANTICAS, true, 1))
        add(Question(86, "¿Cuál ha sido tu aniversario más especial?", Category.ROMANTICAS, true, 1))
        add(Question(87, "¿Qué apodo cariñoso le tienes a tu pareja?", Category.ROMANTICAS, true, 1))
        add(Question(88, "¿Qué harías si tu pareja se muda lejos?", Category.ROMANTICAS, true, 2))
        add(Question(89, "¿Crees en el destino con tu pareja?", Category.ROMANTICAS, true, 1))
        add(Question(90, "¿Qué le dirías a tu pareja si fuera la última vez que la ves?", Category.ROMANTICAS, true, 2))

        // ==================== 💑 ROMÁNTICAS (Soltero) - 10 ====================
        add(Question(91, "¿Cómo sería tu cita ideal?", Category.ROMANTICAS, false, 1))
        add(Question(92, "¿Qué te enamora de una persona?", Category.ROMANTICAS, false, 1))
        add(Question(93, "¿Has tenido un amor imposible?", Category.ROMANTICAS, false, 2))
        add(Question(94, "¿Qué es lo más romántico que has hecho?", Category.ROMANTICAS, false, 1))
        add(Question(95, "¿Crees que encontrarás el amor este año?", Category.ROMANTICAS, false, 1))
        add(Question(96, "¿Qué carta de amor nunca enviaste?", Category.ROMANTICAS, false, 2))
        add(Question(97, "¿Cuál es tu película romántica favorita y por qué?", Category.ROMANTICAS, false, 1))
        add(Question(98, "¿Qué harías si tu crush te invita a salir mañana?", Category.ROMANTICAS, false, 1))
        add(Question(99, "¿Te has enamorado a primera vista?", Category.ROMANTICAS, false, 1))
        add(Question(100, "¿Qué significa el amor para ti?", Category.ROMANTICAS, false, 1))

        // ==================== 🎭 CONFESIONES (Universal) - 20 ====================
        add(Question(101, "¿Cuál es tu mayor arrepentimiento amoroso?", Category.CONFESIONES, null, 2))
        add(Question(102, "¿Has engañado o te han engañado?", Category.CONFESIONES, null, 3))
        add(Question(103, "¿Qué nunca le contarías a tu pareja/crush?", Category.CONFESIONES, null, 3))
        add(Question(104, "¿Has hablado mal de tu pareja con amigos?", Category.CONFESIONES, null, 2))
        add(Question(105, "¿Cuál es la mentira más grande que has dicho en una relación?", Category.CONFESIONES, null, 3))
        add(Question(106, "¿Has vuelto con un ex solo por soledad?", Category.CONFESIONES, null, 2))
        add(Question(107, "¿Qué es lo que más te duele de tu última ruptura?", Category.CONFESIONES, null, 2))
        add(Question(108, "¿Has sido tóxico en alguna relación?", Category.CONFESIONES, null, 2))
        add(Question(109, "¿Qué secreto te da vergüenza admitir?", Category.CONFESIONES, null, 3))
        add(Question(110, "¿Has sentido celos de alguien de este grupo?", Category.CONFESIONES, null, 2))
        add(Question(111, "¿Cuál es tu mayor inseguridad en el amor?", Category.CONFESIONES, null, 2))
        add(Question(112, "¿Has fingido estar enamorado?", Category.CONFESIONES, null, 3))
        add(Question(113, "¿Qué harías si descubres que tu pareja te miente?", Category.CONFESIONES, null, 2))
        add(Question(114, "¿Has llorado por amor en el último mes?", Category.CONFESIONES, null, 1))
        add(Question(115, "¿Qué es lo que más extrañas de tu ex?", Category.CONFESIONES, null, 2))
        add(Question(116, "¿Has hecho algo ilegal por amor?", Category.CONFESIONES, null, 3))
        add(Question(117, "¿Te arrepientes de no haber besado a alguien?", Category.CONFESIONES, null, 1))
        add(Question(118, "¿Cuál es tu pensamiento impuro más recurrente?", Category.CONFESIONES, null, 3))
        add(Question(119, "¿Has usado a alguien para dar celos?", Category.CONFESIONES, null, 2))
        add(Question(120, "¿Qué es lo peor que te han hecho en una relación?", Category.CONFESIONES, null, 2))
    }

    fun getFilteredQuestions(
        selectedCategories: Set<Category>,
        intensityLevel: Int, // 1=Suave, 2=Medio, 3=Extremo
        playerHasPartner: Boolean?
    ): List<Question> {
        return getAllQuestions().filter { question ->
            // Filtrar por categorías seleccionadas
            val categoryMatch = if (selectedCategories.isEmpty()) true else question.category in selectedCategories

            // Filtrar por intensidad (si intensidad es 1, solo preguntas 1; si 2, 1 y 2; si 3, todas)
            val intensityMatch = question.intensity <= intensityLevel

            // Filtrar por estado de pareja
            val partnerMatch = when (question.forPartnered) {
                null -> true
                true -> playerHasPartner == true
                false -> playerHasPartner == false
            }

            categoryMatch && intensityMatch && partnerMatch
        }.shuffled()
    }

    fun getRandomQuestion(
        selectedCategories: Set<Category>,
        intensityLevel: Int,
        playerHasPartner: Boolean?
    ): Question? {
        return getFilteredQuestions(selectedCategories, intensityLevel, playerHasPartner).randomOrNull()
    }
}
