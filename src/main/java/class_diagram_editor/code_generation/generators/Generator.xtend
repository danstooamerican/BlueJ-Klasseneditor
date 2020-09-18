package class_diagram_editor.code_generation.generators

abstract class Generator<T> {

    def String generate(T type);

}