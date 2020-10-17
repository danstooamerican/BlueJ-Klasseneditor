package class_diagram_editor.code_generation.generators

import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Connectable;

class InterfaceGenerator extends Generator {

    def String generate(InterfaceModel interfaceModel) '''
        public interface «interfaceModel.getName()»«
            IF interfaceModel.isExtending()» extends «
                FOR Connectable connectable : interfaceModel.getExtendsRelations() SEPARATOR ', '»«
                    connectable.getName()»«
                ENDFOR»«
            ENDIF» {
            «FOR MethodModel methodModel : interfaceModel.getMethods() SEPARATOR '\n'»
                «generateInterfaceMethodSignature(methodModel).trim()»;
            «ENDFOR»
        }
    '''

}