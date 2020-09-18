package class_diagram_editor.presentation.main_screen.skins.generators

import class_diagram_editor.diagram.AttributeModel;

class UMLAttributeGenerator {

    def String generate(AttributeModel attributeModel) '''
      «attributeModel.getVisibility().getSymbol()» «attributeModel.getName()» : «attributeModel.getType()»«IF attributeModel.isFinal()» {readOnly}«ENDIF»
    '''

}