package class_diagram_editor.code_generation;

/**
 * Represents a code element which can be used to generate source code.
 */
public interface CodeElement {

    /**
     * @return the name of the element.
     */
    String getName();

    /**
     * @return the name of the element when it was generated the last time.
 *             The last generated name is equal to {@code getName()}
     *         if the element did not accept a {@link JavaCodeGenerator} before.
     */
    String getLastGeneratedName();

    /**
     * Calls the appropriate method on the
     * {@link JavaCodeGenerator code generator} to generate the source code for this element.
     *
     * @param codeGenerator the {@link JavaCodeGenerator code generator} responsible for generating the source code.
     */
    void accept(JavaCodeGenerator codeGenerator);

}
