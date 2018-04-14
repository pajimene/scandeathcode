package com.capgemini.coe.scandeathcode;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import com.capgemini.coe.scandeathcode.data.ClassInfo;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws Exception {
		new App().parse1();
	}

	public void parse1() throws Exception {
		FileInputStream in = new FileInputStream("D:/GitHub/jenkins_view/data/src/main/java/com/capgemini/jenkinsdata/projectmanagement/logic/impl/ProjectmanagementImpl.java");

		CompilationUnit cu;
		try {

			//cu = JavaParser.parse(new File("D:/GitHub/jenkins_view/data/src/main/java/com/capgemini/jenkinsdata/projectmanagement/logic"));

			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}

		ClassInfo classInfo = null;
		TypeSolver typeSolver = new ReflectionTypeSolver();

		cu.getClassByName("String");

		for (Node node : cu.getChildNodes()) {

			if (node instanceof PackageDeclaration) {
				classInfo = new ClassInfo(((PackageDeclaration) node).getNameAsString());
				continue;
			}

			if (classInfo == null)
				continue;

			if (node instanceof ImportDeclaration) {
				classInfo.addImport(((ImportDeclaration) node).getNameAsString());
				continue;
			}

			if (node instanceof ClassOrInterfaceDeclaration) {

				ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration) node;

				for (Node node1 : declaration.getMembers()) {

					if (node1 instanceof MethodDeclaration) {
						MethodDeclaration method = (MethodDeclaration) node1;

						Modifier modifier = null;

						if (method.getModifiers().contains(Modifier.PUBLIC))
							modifier = Modifier.PUBLIC;
						if (method.getModifiers().contains(Modifier.PRIVATE))
							modifier = Modifier.PRIVATE;
						if (method.getModifiers().contains(Modifier.PROTECTED))
							modifier = Modifier.PROTECTED;

						classInfo.addMethod(modifier, method.getName(), method.getType(), method.getParameters());

						//System.out.println(isPublic + " : " + method.getType() + " : " + method.getName() + " : " + method.getParameters());
					}

				}

				//System.out.println(node.getMetaModel().toString());
			}

		}

		//new MethodVisitor().visit(cu, null);
	}

	private static class MethodVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(MethodCallExpr methodCall, Object arg) {
			System.out.print("Method call: " + methodCall.getName() + "\n");
			List<Expression> args = methodCall.getArguments();
			if (args != null)
				handleExpressions(args);
		}

		private void handleExpressions(List<Expression> expressions) {
			for (Expression expr : expressions) {

				//System.out.println("\t" + expr.tygetClass());

				if (expr instanceof MethodCallExpr)
					visit((MethodCallExpr) expr, null);
				else if (expr instanceof BinaryExpr) {
					BinaryExpr binExpr = (BinaryExpr) expr;
					handleExpressions(Arrays.asList(binExpr.getLeft(), binExpr.getRight()));
				}
			}
		}
	}
}
