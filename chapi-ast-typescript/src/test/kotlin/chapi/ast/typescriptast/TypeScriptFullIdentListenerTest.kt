package chapi.ast.typescriptast

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TypeScriptFullIdentListenerTest {
    @Test
    internal fun shouldCompile() {
        var code = """
interface IPerson {
    name: string;
}            
"""
        TypeScriptAnalyser().analysis(code, "")
    }

    @Test
    internal fun shouldIdentifyInterfaceName() {
        var code = """
export interface IPerson {
    name: string;
    gender: string;
}
"""
        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.DataStructures.size, 1)
        assertEquals(codeFile.DataStructures[0].NodeName, "IPerson")
    }

    @Test
    internal fun shouldIdentifyInnerClass() {
        var code = """
class Foo {
    static Bar = class {

    }
}
"""
        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.DataStructures.size, 1)
        assertEquals(codeFile.DataStructures[0].NodeName, "Foo")
    }

    @Test
    internal fun shouldIdentifyMultipleNode() {
        var code = """
interface IPerson {
    name: string;
}

class Person implements IPerson {
    public publicString: string;
    private privateString: string;
    protected protectedString: string;
    readonly readonlyString: string;
    name: string;

    constructor(name: string) {
        this.name = name;
    }
}
"""
        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.DataStructures.size, 2)
        assertEquals(codeFile.DataStructures[0].NodeName, "IPerson")
        assertEquals(codeFile.DataStructures[1].NodeName, "Person")
    }

    @Test
    internal fun shouldIdentifyImplements() {
        var code = """
class Person implements IPerson {
    public publicString: string;
    private privateString: string;
    protected protectedString: string;
    readonly readonlyString: string;
    name: string;

    constructor(name: string) {
        this.name = name;
    }
}
"""
        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.DataStructures.size, 1)
        assertEquals(codeFile.DataStructures[0].Implements[0], "IPerson")
    }

    @Test
    internal fun shouldIdentifyClassExtends() {
        var code = """
class Employee extends Person {

}"""
        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.DataStructures.size, 1)
        assertEquals(codeFile.DataStructures[0].Extend, "Person")
    }

    @Test
    internal fun shouldIdentifyInterfaceExtends() {
        var code = """
interface IEmployee extends IPerson {
    empCode: number;
    readonly empName: string;
    empDept?:string;
    getSalary: (number) => number; // arrow function
    getManagerName(number): string;
}
"""
        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.DataStructures.size, 1)
        assertEquals(codeFile.DataStructures[0].Extend, "IPerson")
    }

    @Test
    internal fun shouldIdentifyBlockImportsSource() {
        var code = """
import { ZipCodeValidator } from "./ZipCodeValidator";

"""

        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "./ZipCodeValidator")
        assertEquals(codeFile.Imports[0].UsageName[0], "ZipCodeValidator")
    }

    @Test
    internal fun shouldIdentifyMultipleBlockImportsSource() {
        var code = """
import { ZipCodeValidator, ZipCodeGenerator } from "./ZipCodeValidator";

"""

        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "./ZipCodeValidator")
        assertEquals(codeFile.Imports[0].UsageName.size, 2)
        assertEquals(codeFile.Imports[0].UsageName[0], "ZipCodeValidator")
        assertEquals(codeFile.Imports[0].UsageName[1], "ZipCodeGenerator")
    }

    @Test
    internal fun shouldIdentifyRequireImport() {
        var code = """
import zip = require("./ZipCodeValidator");

"""

        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "./ZipCodeValidator")
        assertEquals(codeFile.Imports[0].UsageName[0], "zip")
    }

    @Test
    internal fun shouldIdentifyImportAll() {
        var code = """
import "./module.js";

"""

        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "./module.js")
    }

    @Test
    internal fun shouldIdentifySpecificSymbol() {
        var code = """
import $ from "jquery";
import _ from "lodash";

"""

        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.Imports.size, 2)
        assertEquals(codeFile.Imports[0].Source, "jquery")
        assertEquals(codeFile.Imports[0].UsageName[0], "$")
        assertEquals(codeFile.Imports[1].Source, "lodash")
        assertEquals(codeFile.Imports[1].UsageName[0], "_")
    }

    @Test
    internal fun shouldIdentifyImportAs() {
        var code = """
import * as validator from "./ZipCodeValidator";

"""

        val codeFile = TypeScriptAnalyser().analysis(code, "")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "./ZipCodeValidator")
        assertEquals(codeFile.Imports[0].UsageName[0], "validator")
    }

    private val personClassCode = """class Person implements IPerson {
        public publicString: string;
        private privateString: string;
        protected protectedString: string;
        readonly readonlyString: string;
        name: string;
    
        constructor(name: string) {
            this.name = name;
        }
    }"""

    @Test
    internal fun shouldIdentifyClassConstructorMethod() {
        val codeFile = TypeScriptAnalyser().analysis(personClassCode, "")
        assertEquals(codeFile.DataStructures[0].Functions.size, 1)
        assertEquals(codeFile.DataStructures[0].Functions[0].Name, "constructor")
        val parameters = codeFile.DataStructures[0].Functions[0].Parameters
        assertEquals(parameters.size, 1)
        assertEquals(parameters[0].TypeValue, "name")
        assertEquals(parameters[0].TypeType, "string")
    }

    @Test
    internal fun shouldIdentifyClassFields() {
        val codeFile = TypeScriptAnalyser().analysis(personClassCode, "")
        assertEquals(codeFile.DataStructures[0].Fields.size, 5)
        assertEquals(codeFile.DataStructures[0].Fields[0].Modifiers[0], "public")
        assertEquals(codeFile.DataStructures[0].Fields[0].TypeValue, "publicString")
        assertEquals(codeFile.DataStructures[0].Fields[0].TypeType, "string")
        assertEquals(codeFile.DataStructures[0].Fields[1].TypeType, "string")
        assertEquals(codeFile.DataStructures[0].Fields[4].Modifiers.size, 0)
    }

    @Test
    internal fun shouldIdentifyNormalClassFunction() {
        val normalClassFunction = """
           
class Employee extends Person {
    empCode: number;
    static pi: number = 3.14;

    constructor(empcode: number, name:string) {
        super(name);
        this.empCode = empcode;
    }

    displayName():void {
        console.log("Name = " + this.name +  ", Employee Code = " + this.empCode);
    }
} 
        """

        val codeFile = TypeScriptAnalyser().analysis(normalClassFunction, "")
        assertEquals(codeFile.DataStructures[0].Functions.size, 2)
        assertEquals(codeFile.DataStructures[0].Functions[1].Name, "displayName")
    }
}