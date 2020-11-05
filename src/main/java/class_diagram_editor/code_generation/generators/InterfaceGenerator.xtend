package class_diagram_editor.code_generation.generators

import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.code_generation.CodeRepository;

class InterfaceGenerator extends Generator {

    def String generate(InterfaceModel interfaceModel, CodeRepository codeRepository) '''
        «codeRepository.getImports()»public interface «interfaceModel.getName()»«
            IF interfaceModel.isExtending()» extends «
                FOR Connectable connectable : interfaceModel.getExtendsRelations() SEPARATOR ', '»«
                    connectable.getName()»«
                ENDFOR»«
            ENDIF» {
            «FOR MethodModel methodModel : interfaceModel.getMethods() SEPARATOR '\n'»
                «codeRepository.getMethodComment(generateInterfaceMethodSignature(methodModel.getLastGenerated()).trim())»
                «generateInterfaceMethodSignature(methodModel).trim()»;
            «ENDFOR»
        }
    '''

}