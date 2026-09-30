
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
package ARVTRIE;

public class Main {

	public static void main(String[] args) {

	    // Exemplo minimo de insercao e consulta de palavras por prefixo.
	    Trie trie = new Trie();

	    trie.inserir("casa");
	    trie.inserir("casaco");
	    trie.inserir("carro");
	    trie.inserir("banana");
	    trie.inserir("cachorro");

	    System.out.println(trie.sugerir("ca"));
	}
}