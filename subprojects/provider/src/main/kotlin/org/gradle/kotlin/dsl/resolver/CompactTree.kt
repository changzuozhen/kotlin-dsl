/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.kotlin.dsl.resolver

import org.gradle.kotlin.dsl.execution.splitIncluding

import java.io.File


internal
fun compactStringFor(files: Iterable<java.io.File>) =
    compactStringFor(files.map { it.path }, File.separatorChar)


internal
fun compactStringFor(paths: Iterable<String>, separator: Char) =
    CompactTree.Companion.of(paths.map { it.splitIncluding(separator).toList() }).toString()


private
sealed class CompactTree {

    companion object {

        fun of(paths: Iterable<List<String>>): CompactTree =
            paths
                .filter { it.isNotEmpty() }
                .groupBy({ it[0] }, { it.drop(1) })
                .map { (label, remaining) ->
                    val subTree = CompactTree.Companion.of(remaining)
                    when (subTree) {
                        is CompactTree.Empty -> CompactTree.Label(label)
                        is CompactTree.Label -> CompactTree.Label(
                            label + subTree.label
                        )
                        is CompactTree.Branch -> CompactTree.Edge(
                            Label(label), subTree
                        )
                        is CompactTree.Edge -> CompactTree.Edge(
                            Label(label + subTree.label), subTree.tree
                        )
                    }
                }.let {
                    when (it.size) {
                        0 -> CompactTree.Empty
                        1 -> it.first()
                        else -> CompactTree.Branch(it)
                    }
                }
    }

    object Empty : CompactTree() {
        override fun toString() = "ø"
    }

    data class Label(val label: String) : CompactTree() {
        override fun toString() = label
    }

    data class Branch(val edges: List<CompactTree>) : CompactTree() {
        override fun toString() = edges.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }

    data class Edge(val label: CompactTree.Label, val tree: CompactTree) : CompactTree() {
        override fun toString() = "$label$tree"
    }
}