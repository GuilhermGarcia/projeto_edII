
// Copyright (C) 2024 André Kishimoto & modified by Jean Laine
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

// Árvore criada no exemplo abaixo:
//
//     A
//   /   \
//  B     C
//   \   /
//    D E
//       \
//        F
//       / \
//      G   H
//
package ARVBIN;

public class Main {

    public static void main(String[] args) {

        BST tree = new BST();

        System.out.println("=== INSERCOES ===");

        tree.insert("Maria");
        tree.insert("Carlos");
        tree.insert("Pedro");
        tree.insert("Ana");
        tree.insert("Joao");
        tree.insert("Rafael");
        tree.insert("Zeca");

        System.out.println("Em ordem: " + tree.inOrderTraversal());

        // teste de duplicata
        tree.insert("Carlos");
        System.out.println("Depois de inserir Carlos novamente:");
        System.out.println("Em ordem: " + tree.inOrderTraversal());


        System.out.println("\n=== BUSCAS ===");

        BTNode encontrado = tree.search("Pedro");

        if (encontrado != null) {
            System.out.println("Pedro encontrado:");
            System.out.println(encontrado);
        } else {
            System.out.println("Pedro nao encontrado.");
        }

        BTNode naoEncontrado = tree.search("Lucas");

        if (naoEncontrado != null) {
            System.out.println("Lucas encontrado:");
            System.out.println(naoEncontrado);
        } else {
            System.out.println("Lucas nao encontrado.");
        }


        System.out.println("\n=== MINIMO E MAXIMO ===");

        System.out.println("Minimo: " + tree.findMin());
        System.out.println("Maximo: " + tree.findMax());


        System.out.println("\n=== PREDECESSOR E SUCESSOR ===");

        System.out.println(
            "Predecessor de Maria: " +
            tree.findPredecessor("Maria")
        );

        System.out.println(
            "Sucessor de Maria: " +
            tree.findSuccessor("Maria")
        );


        System.out.println("\n=== REMOCAO DE FOLHA ===");

        tree.remove("Ana");

        System.out.println(
            "Em ordem depois de remover Ana: "
            + tree.inOrderTraversal()
        );


        System.out.println("\n=== REMOCAO DE NO COM UM FILHO ===");

        tree.remove("Rafael");

        System.out.println(
            "Em ordem depois de remover Rafael: "
            + tree.inOrderTraversal()
        );


        System.out.println("\n=== REMOCAO DE NO COM DOIS FILHOS ===");

        tree.remove("Maria");

        System.out.println(
            "Em ordem depois de remover Maria: "
            + tree.inOrderTraversal()
        );


        System.out.println("\n=== REMOCAO DE NO INEXISTENTE ===");

        tree.remove("Alberto");

        System.out.println(
            "Em ordem: "
            + tree.inOrderTraversal()
        );


        System.out.println("\n=== INFORMACOES DOS NOS ===");

        tree.printNodeInfo();


        System.out.println("\n=== CLEAR ===");

        tree.clear();

        System.out.println(
            "Arvore vazia? "
            + tree.isEmpty()
        );
    }
}