package class_diagram_editor.code_generation.generators

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.MethodModel;

abstract class Generator<T> {

    def String generate(T type);

    def String generateAttribute(AttributeModel attributeModel) '''
        «attributeModel.getVisibility().getCode()»«IF attributeModel.isStatic()» static«ENDIF»«IF attributeModel.isFinal()» final«ENDIF» «attributeModel.getType()» «attributeModel.getName()»;
    '''

    def String generateMethod(MethodModel methodModel) '''
        «methodModel.getVisibility().getCode()»«IF methodModel.isStatic()» static«ENDIF»«IF methodModel.isAbstract()» abstract«ENDIF» «methodModel.getReturnType()» «methodModel.getName()»(«FOR AttributeModel attribute : methodModel.getAttributes() SEPARATOR ', '»«attribute.getType()» «attribute.getName()»«ENDFOR») {

        }
    '''

}