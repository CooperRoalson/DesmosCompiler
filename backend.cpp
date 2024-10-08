//
// Created by Cooper Roalson on 7/27/24.
//

#include <iostream>

#include "compiler.h"
#include "ast.h"

using namespace AST;

void Compiler::compile_backend(std::ostream& out) {
    ast->compile(out);
}

static void compile_identifier(std::ostream& out, std::string_view identifier, bool isConst, bool isFunction) {
    out << (isFunction ? "F" : isConst ? "C" : "V") << "_{" << identifier << "}";
}

static void compile_simple_binop(std::ostream& out, const BinaryOperatorNode* node, const char* op, bool commutative = true) {
    if (node->left->precedence() > node->precedence()) {
        out << "\\left(";
        node->left->compile(out);
        out << "\\right)";
    } else {
        node->left->compile(out);
    }
    out << op;
    if (node->right->precedence() > node->precedence() || (!commutative && node->right->precedence() == node->precedence())) {
        out << "\\left(";
        node->right->compile(out);
        out << "\\right)";
    } else {
        node->right->compile(out);
    }
}


void LiteralNode::compile(std::ostream& out) const {
    out << value;
}

void IdentifierNode::compile(std::ostream& out) const {
    compile_identifier(out, identifier, type.isConst, false);
}

void DeclarationNode::compile(std::ostream& out) const {
    compile_identifier(out, identifier, type.isConst, false);
}

void FunctionDeclarationNode::compile(std::ostream& out) const {
    compile_identifier(out, identifier, false, true);
}

int BinaryOperatorNode::precedence() const {
    switch (op) {
        case Operator::EXP:
            return 2;
        case Operator::MUL:
        case Operator::DIV:
        case Operator::MOD:
            return 4;
        case Operator::PLUS:
        case Operator::MINUS:
            return 5;
        case Operator::AND:
            return 8;
        case Operator::OR:
            return 9;
        default:
            throw std::runtime_error("Invalid binary operator: " + std::to_string(op));
    }
}

void BinaryOperatorNode::compile(std::ostream& out) const {
    switch (op) {
        case Operator::PLUS:
            compile_simple_binop(out, this, "+"); break;
        case Operator::MINUS:
            compile_simple_binop(out, this, "-", false); break;
        case Operator::MUL:
            compile_simple_binop(out, this, "\\cdot "); break;
        case Operator::DIV:
            out << "\\frac{";
            left->compile(out);
            out << "}{";
            right->compile(out);
            out << "}";
            break;
        case Operator::MOD:
            out << "\\operatorname{mod}\\left(";
            left->compile(out);
            out << ",";
            right->compile(out);
            out << "\\right)";
            break;
        case Operator::EXP:
            if (left->precedence() > 0) {
                out << "\\left(";
                left->compile(out);
                out << "\\right)";
            } else {
                left->compile(out);
            }
            out << "^{";
            right->compile(out);
            out << "}";
            break;
        case Operator::AND:
            compile_simple_binop(out, this, "\\cdot "); break;
        case Operator::OR:
            out << "\\max\\left(";
            left->compile(out);
            out << ",";
            right->compile(out);
            out << "\\right)";
            break;
        default:
            throw std::runtime_error("Invalid binary operator: " + std::to_string(op));
    }
}

int UnaryOperatorNode::precedence() const {
    return op == Operator::INVERT ? 5 : 3;
}

void UnaryOperatorNode::compile(std::ostream& out) const {
    switch (op) {
        case Operator::MINUS:
            out << "-";
            if (expr->precedence() > precedence()) {
                out << "\\left(";
                expr->compile(out);
                out << "\\right)";
            } else {
                expr->compile(out);
            }
            break;
        case Operator::INVERT:
            out << "1-";
            if (expr->precedence() >= precedence()) {
                out << "\\left(";
                expr->compile(out);
                out << "\\right)";
            } else {
                expr->compile(out);
            }
            break;
        default:
            throw std::runtime_error("Invalid unary operator: " + std::to_string(op));
    }
}

void StatementBlockNode::compile(std::ostream& out) const {
    for (auto& statement : statements) {
        statement->compile(out);
    }
}

void InitializationStatementNode::compile(std::ostream& out) const {
    declaration->compile(out);
    out << " = ";
    value->compile(out);
    out << std::endl;
}

void MainBlockNode::compile(std::ostream& out) const {
    for (auto& statement : statements) {
        statement->compile(out);
    }
}
