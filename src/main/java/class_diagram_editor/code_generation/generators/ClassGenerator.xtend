package class_diagram_editor.diagram.code_generation

import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Connectable;
import java.util.Map;
import class_diagram_editor.code_generation.generators.Generator;
import class_diagram_editor.code_generation.CodeRepository;

class ClassGenerator extends Generator {

    def String generate(ClassModel c, CodeRepository codeRepository) '''
        «codeRepository.getImports()»public«
            IF c.isAbstract()» abstract«ENDIF» class «c.getName()»«IF c.isExtending()» extends «
                FOR Connectable connectable : c.getExtendsRelations() SEPARATOR ', '»«
                    connectable.getName()»«
                ENDFOR»«
            ENDIF»«
            IF c.isImplementingInterfaces()» implements «
                FOR InterfaceModel interfaceModel : c.getImplementsInterfaces() SEPARATOR ', '»«
                    interfaceModel.getName()»«
                ENDFOR»«
            ENDIF» {
            «FOR AttributeModel attributeModel : c.getAttributes()»
                «generateAttribute(attributeModel).trim()»
            «ENDFOR»«
            IF c.hasAssociations()»
                «IF c.hasAttributes()»

                «ENDIF»
                «FOR Map.Entry<String, Connectable> connectable : c.getAssociations().entrySet()»
                private «connectable.getValue().getName()» «connectable.getKey()»;
                «ENDFOR»
            «ENDIF»«
            FOR MethodModel methodModel : c.getMethods() BEFORE '\n' SEPARATOR '\n\n'»«
                codeRepository.getMethodComment(generateMethodSignature(methodModel.getLastGenerated()).trim())
            »
            «generateMethodSignature(methodModel).trim()»«IF methodModel.isAbstract()»;«ELSE» {
                «codeRepository.getMethodBody(generateMethodSignature(methodModel.getLastGenerated()).trim())»
            }«ENDIF»«
            ENDFOR»«
            IF c.isExtending() && !c.isAbstract()»«
            FOR MethodModel methodModel : c.getExtendsClass().getMethods() SEPARATOR '\n\n'»«
                IF methodModel.isAbstract()»
            «codeRepository.getMethodComment(generateOverrideMethodSignature(methodModel.getLastGenerated()).trim())»
            @Override
            «generateOverrideMethodSignature(methodModel).trim()» {
                «codeRepository.getMethodBody(generateOverrideMethodSignature(methodModel.getLastGenerated()).trim())»
            }«ENDIF»«ENDFOR»«
            ENDIF»«
            FOR InterfaceModel interfaceModel : c.getImplementsInterfaces() BEFORE '\n\n'»«
                FOR MethodModel methodModel : interfaceModel.getMethodsWithExtending() SEPARATOR '\n\n'»
            «codeRepository.getMethodComment(generateMethodSignature(methodModel.getLastGenerated()).trim())»
            @Override
            «generateMethodSignature(methodModel).trim()» {
                «codeRepository.getMethodBody(generateMethodSignature(methodModel.getLastGenerated()).trim())»
            }«
                ENDFOR»«
            ENDFOR»«
            FOR AttributeModel attributeModel : c.getAttributes() BEFORE '\n\n' SEPARATOR '\n\n'»«
                IF attributeModel.hasGetter()»
                    «generateMethodSignature(attributeModel.getGetter()).trim()» {
                        return this.«attributeModel.getName()»;
                    }«
                ENDIF»«
                IF attributeModel.hasSetter()»
                    «generateMethodSignature(attributeModel.getSetter()).trim()» {
                        this.«attributeModel.getName()» = «attributeModel.getName()»;
                    }«
                ENDIF»«
            ENDFOR»
        }
    '''

}