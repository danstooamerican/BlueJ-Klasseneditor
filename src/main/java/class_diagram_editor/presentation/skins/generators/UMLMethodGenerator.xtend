package class_diagram_editor.presentation.main_screen.skins.generators

import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.AttributeModel;

class UMLMethodGenerator {

    def String generate(MethodModel methodModel) '''
      «methodModel.getVisibility().getSymbol()» «IF methodModel.isConstructor()»<<create>> «ENDIF»«methodModel.getName()»(«FOR AttributeModel attribute : methodModel.getParameters() SEPARATOR ', '»«attribute.getName()» : «attribute.getType()»«ENDFOR»)«IF methodModel.hasReturnType()» : «methodModel.getReturnType()»«ENDIF»
    '''

}