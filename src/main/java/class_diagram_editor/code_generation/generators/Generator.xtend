package class_diagram_editor.code_generation.generators

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.MethodModel;

abstract class Generator {

    def String generateAttribute(AttributeModel attributeModel) '''
        «attributeModel.getVisibility().getCode()»«
        IF attributeModel.isStatic()» static«ENDIF»«
        IF attributeModel.isFinal()» final«ENDIF» «attributeModel.getType()» «attributeModel.getName()»;
    '''

    def String generateMethodSignature(MethodModel methodModel) '''
        «methodModel.getVisibility().getCode()»«
        IF methodModel.isStatic()» static«ENDIF»«
        IF methodModel.isAbstract()» abstract«ENDIF»«
        IF methodModel.hasReturnType()» «methodModel.getReturnType()»«
        ELSEIF !methodModel.isConstructor()» void«
        ENDIF» «methodModel.getName()»(«
        FOR AttributeModel attribute : methodModel.getParameters() SEPARATOR ', '»«
            attribute.getType()» «attribute.getName()»«
        ENDFOR»)
    '''

    def String generateInterfaceMethodSignature(MethodModel methodModel) '''
        «IF methodModel.isStatic()» static«ENDIF»«
        IF methodModel.isAbstract()» abstract«ENDIF»«
        IF methodModel.hasReturnType()» «methodModel.getReturnType()»«
        ELSEIF !methodModel.isConstructor()» void«
        ENDIF» «methodModel.getName()»(«
        FOR AttributeModel attribute : methodModel.getParameters() SEPARATOR ', '»«
            attribute.getType()» «attribute.getName()»«
        ENDFOR»)
    '''

}