package class_diagram_editor.diagram.code_generation

import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Connectable;
import java.util.Map;
import class_diagram_editor.code_generation.generators.Generator;

class ClassGenerator extends Generator<ClassModel> {

    override String generate(ClassModel c) '''
        public«IF c.isAbstract()» abstract«ENDIF» class «c.getName()»«IF c.isExtending()» extends «FOR Connectable connectable : c.getExtendsRelations() SEPARATOR ', '»«connectable.getName()»«ENDFOR»«ENDIF»«IF c.isImplementingInterfaces()» implements «FOR InterfaceModel interfaceModel : c.getImplementsInterfaces() SEPARATOR ', '»«interfaceModel.getName()»«ENDFOR»«ENDIF» {
            «FOR AttributeModel attributeModel : c.getAttributes()»
                «generateAttribute(attributeModel).trim()»
            «ENDFOR»
            «IF c.hasAssociations()»
                «IF c.hasAttributes()»

                «ENDIF»
                «FOR Map.Entry<String, Connectable> connectable : c.getAssociations().entrySet()»
                    private «connectable.getValue().getName()» «connectable.getKey()»;
                «ENDFOR»
            «ENDIF»
            «IF (c.hasAttributes() || c.hasAssociations()) && c.hasMethods()»

            «ENDIF»
            «FOR MethodModel methodModel : c.getMethods() SEPARATOR '\n'»
                «generateMethodSignature(methodModel).trim()»«IF methodModel.isAbstract()»;«ELSE» {

                }«ENDIF»
            «ENDFOR»

            «FOR AttributeModel attributeModel : c.getAttributes() SEPARATOR '\n'»
                «IF attributeModel.hasGetter()»
                    «generateMethodSignature(attributeModel.getGetter()).trim()» {
                        return this.«attributeModel.getName()»;
                    }
                «ENDIF»
                «IF attributeModel.hasGetter() && attributeModel.hasSetter()»

                «ENDIF»
                «IF attributeModel.hasSetter()»
                    «generateMethodSignature(attributeModel.getSetter()).trim()» {
                        this.«attributeModel.getName()» = «attributeModel.getName()»;
                    }
                «ENDIF»
            «ENDFOR»
        }
    '''

}