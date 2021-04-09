package org.imp.jvm.parser.visitor;

import org.imp.jvm.ImpParser;
import org.imp.jvm.ImpParserBaseVisitor;
import org.imp.jvm.domain.ImpFile;
import org.imp.jvm.domain.root.ClassUnit;
import org.imp.jvm.domain.root.StaticUnit;
import org.imp.jvm.domain.scope.Scope;
import org.imp.jvm.domain.statement.Function;
import org.imp.jvm.domain.statement.Statement;
import org.imp.jvm.domain.statement.VariableDeclaration;
import org.imp.jvm.domain.statement.variable.Declaration;
import org.imp.jvm.parser.visitor.statement.StatementVisitor;

import java.util.List;

public class ImpFileVisitor extends ImpParserBaseVisitor<ImpFile> {

    @Override
    public ImpFile visitProgram(ImpParser.ProgramContext ctx) {
        // get all top level statements in the file
        List<ImpParser.StatementContext> statementContexts = ctx.statement();


        // static unit for all non-class statements in the file
        var staticUnit = new StaticUnit("static unit");

        // create an ImpFile node with appropriate children
        var impFile = new ImpFile(staticUnit);

        // handle each statement appropriately
        StatementVisitor statementVisitor = new StatementVisitor(new Scope());
        for (var statement : statementContexts) {
            Statement s = statement.accept(statementVisitor);


            // Split classes out to their own files
            if (s instanceof ClassUnit) {

            } else {
                System.out.println(s);
                System.out.println(s instanceof VariableDeclaration);
                // For everything else, add to the static class.
                if (s instanceof VariableDeclaration) {
                    VariableDeclaration variableDeclaration = (VariableDeclaration) s;
                    Declaration declaration = variableDeclaration.declaration;

                    // Add variable signature to static unit
                    staticUnit.properties.add(declaration);

                    // Initialize variable in static block
                    staticUnit.staticInitializations.add(variableDeclaration);
                } else if (s instanceof Function) {
                    Function f = (Function) s;

                    // add function to static class methods
                    staticUnit.functions.add(f);
                }

            }

            System.out.println(staticUnit);

        }

        // Visit the Static Unit
//        StaticUnitVisitor staticUnitVisitor = new StaticUnitVisitor();
//        StaticUnit staticUnit = staticUnitVisitor.

        // Visit each Class Unit

        return impFile;
    }

}
