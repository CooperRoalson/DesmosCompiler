#include <iostream>
#include <fstream>
#include <sstream>

#include "compiler.h"
#include "ast.h"

int main() {
    std::ifstream inFile("test.des", std::ios_base::in);
    if (!inFile.is_open()) {
        std::cerr << "Error: Could not open inFile" << std::endl;
        return 1;
    }

    std::stringstream buffer;
    buffer << inFile.rdbuf();
    std::string source = buffer.str();

    std::stringstream result;
    if (compile_program(source, result)) {
        std::ofstream outFile("test.out", std::ios_base::out);
        outFile << result.str();
        outFile.close();
        std::cout << "Successfully wrote output to test.out" << std::endl;
    }

    // For testing:
    std::cout << "Compiling to cout:" << std::endl;
    compile_program(source, std::cout);

    return 0;
}

bool compile_program(std::string& source, std::ostream& out) {
    Compiler compiler;
    if (!compiler.compile_frontend(source)) {return false;}
    compiler.compile_backend(out);
    return true;
}

Compiler::Compiler() : ast(), symbolTable() {}