package p1admin.adminDB;

import java.util.List;

import javax.sql.DataSource;

import p1admin.model.Opcion;
import p1admin.model.Pregunta;

/**
 * Fachada de acceso a la base de datos.
 * 
 * Implementa aquí las operaciones de acceso a datos.
 * 
 * AVISO: No incluyas ninguna sentencia SQL ni ninguna referencia a una tabla o
 * columna de la BD. Los accesos a la BD deben hacerse a traves de un Mapper.
 * 
 */
public class DBFacade implements GenericDBFacade<Pregunta, Opcion> {

    private PreguntaMapper pm;
    private OptionMapper oMapper;

    public DBFacade(DataSource ds) {
        this.pm = new PreguntaMapper(ds);
        this.oMapper = new OptionMapper(ds);
    }

    /**
     * Inserta una pregunta en la base de datos.
     *
     * Esta función no tiene que guardar las opciones de la pregunta
     * en la base de datos.
     *
     * Esta función es llamada cuando el usuario hace clic en el botón
     * {@code Añadir Pregunta}, justo tras introducir el enunciado de la misma.
     *
     * @param question
     *            Pregunta a almacenar en la BD.
     */
    @Override
    public void insertQuestion(Pregunta question) {
        if (!pm.insert(question)){
            System.out.println("Problema al insertar pregunta en BD: " + question);
        }
    }

	/**
     * Devuelve todas las preguntas de la base de datos.
     *
     * Esta función es llamada al principio del programa, para llenar el JList
     * de la ventana principal. También se llama cuando el usuario ha hecho clic
     * en el botón {@code Filtrar} tras introducir la cadena vacía en el cuadro
     * de texto correspondiente.
     *
     * <b>Importante:</b> Esta función también ha de recuperar de la BD
     * las opciones asociadas a cada pregunta.
     *
     * @return Una lista con todas las preguntas de la base de datos
     */
    @Override
    public List<Pregunta> getAllQuestions() {
        return pm.selectAllWithOptions();
    }

    /**
     * Devuelve todas las preguntas de la base de datos cuyo enunciado contenga
     * la cadena pasada como parámetro.
     *
     * Esta función es llamada cuando el usuario pulsa el botón {@code Filtrar}
     * tras haber introducido una cadena de texto en el cuadro correspondiente.
     *
     * <b>Importante:</b> Esta función también ha de recuperar de la BD
     * las opciones asociadas a cada pregunta devuelta.
     *
     * @param text
     *            Texto que ha de aparecer en las respuestas devueltas.
     * @return Lista con las preguntas cuyo enunciado contengan la cadena
     *         {@code text}.
     */
    @Override
    public List<Pregunta> findQuestionsContaining(String text) {
        return pm.selectAllWithOptions(text);
    }

    /**
     * Actualiza en la BD la información de la pregunta pasada como parámetro.
     *
     * Esta función no tiene que actualizar la información de las posibles
     * respuestas asociadas a dicha pregunta. Tan sólo hay que modificar la
     * tabla que contiene la información de la pregunta en sí.
     *
     * Se supone que la pregunta ya existe en la base de datos.
     *
     * Esta función es llamada cuando el usuario cambia el botón del enunciado
     * de una pregunta.
     *
     * @param question
     *            Pregunta a actualizar.
     */
    @Override
    public void updateQuestion(Pregunta question) {
        pm.update(question);
    }

    /**
     * Actualiza en la BD la información de la respuesta (esto es, una de las
     * posibles opciones asociadas a una pregunta) pasada como parámetro.
     *
     * Esta función es llamada cuando el usuario pulsa el botón {@code Editar}
     * del cuadro de diálogo de edición de pregunta.
     *
     * En función del diseño de la base de datos que hayáis hecho, es posible
     * que el parámetro {@code question} no sea necesario.
     *
     * @param question
     *            Pregunta que contiene la respuesta a actualizar.
     * @param answer
     *            Respuesta a actualizar.
     */
    @Override
    public void updateAnswer(Pregunta question, Opcion answer) {
        System.out.println("Actualizar opción " + answer);

        if (question.getId() == null) {
            question = getLastMatching(question.getEnunciado());
        }

        this.oMapper.update(new Object[] {
            answer.getNumeroOrden(),
            answer.getTexto()
        }, new Object[] {
            question.getId(),
            answer.getNumeroOrden()
        });
    }

    /**
     * Elimina de la BD la respuesta (u opción) pasada como parámetro.
     *
     * Esta función es llamada cuando el usuario pulsa el botón {@code Eliminar}
     * del cuadro de diálogo de edición de pregunta.
     *
     * @param question
     *            Pregunta que contiene la respuesta a eliminar (puede no ser
     *            necesario su uso).
     * @param answer
     *            Respuesta a eliminar.
     */
    @Override
    public void deleteAnswer(Pregunta question, Opcion answer) {
        System.out.println("Eliminar opción " + answer);

        if (question.getId() == null) {
            question = getLastMatching(question.getEnunciado());
        }

        this.oMapper.delete(new Object[] {
            question.getId(),
            answer.getNumeroOrden()
        });
    }

    /**
     * Elimina de la BD la pregunta pasada como parámetro.
     *
     * Esta función es llamada cuando el usuario hace clic en el botón
     * {@code Eliminar pregunta} tras seleccionar una pregunta en la lista
     * principal.
     *
     * Esta función <b>sí</b> ha de eliminar la lista de respuestas asociadas
     * a la pregunta.
     *
     * @param question
     *            Pregunta a eliminar.
     */
    @Override
    public void deleteQuestion(Pregunta question) {
        System.out.println("Eliminar pregunta " + question);

        if (question.getId() == null) {
            question = getLastMatching(question.getEnunciado());
        }

        this.pm.delete(new Object[] {
            question.getId()
        });
    }

    /**
     * Inserta una respuesta en la BD.
     *
     * Esta función es llamada cuando el usuario añade una nueva opción en la
     * pregunta actual.
     *
     * @param question
     *            Pregunta a la que pertenece la acción.
     * @param answer
     *            Respuesta a insertar.
     */
    @Override
    public void insertAnswer(Pregunta question, Opcion answer) {
        System.out.println("Insertar " + answer);

        if (question.getId() == null) {
            answer.setPreguntaMadre(getLastMatching(question.getEnunciado()));
        }

        this.oMapper.insert(answer);
    }

    private Pregunta getLastMatching(String text) {
        List<Pregunta> candidates = findQuestionsContaining(text);
        return candidates.get(candidates.size() - 1);
    }
}
