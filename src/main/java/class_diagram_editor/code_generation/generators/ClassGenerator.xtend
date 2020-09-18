package class_diagram_editor.diagram.code_generation

import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.code_generation.generators.Generator;

class ClassGenerator extends Generator<ClassModel> {

    override String generate(ClassModel c) '''
        public «IF c.isAbstract()»abstract «ENDIF»class «c.getName()» «IF c.isExtending()»extends «c.getExtendsClass().getName()» «ENDIF»{

        }
    '''

}