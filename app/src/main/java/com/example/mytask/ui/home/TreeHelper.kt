package com.example.mytask.ui.home

import timber.log.Timber

object TreeHelper {
    /**
     * 根据所有节点获取可见节点
     */
    fun filterVisibleNode(allNodes: MutableList<Node>): MutableList<Node> {
        val visibleNodes: MutableList<Node> = ArrayList()
        for (node in allNodes) {
            // 如果为根节点，或者上层目录为展开状态
            if (node.isRoot || node.isParentExpand) {
                visibleNodes.add(node)
            }
        }
        return visibleNodes
    }

    /**
     * 获取排序的所有节点
     */
    fun getSortedNodes(nodeList: MutableList<Node>): MutableList<Node> {
        val sortedNodes: MutableList<Node> = ArrayList()
        val nodes = buildTreeNodes(nodeList)
        // 拿到根节点
        val rootNodes = getRootNodes(nodes)
        // 排序以及设置Node间关系
        for (node in rootNodes) {
            addNode(sortedNodes, node, 1)
        }
        return sortedNodes
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private fun addNode(nodes: MutableList<Node>, node: Node, currentLevel: Int) {
        nodes.add(node)
        if (node.isLeaf) return
        for (i in node.childrenNodes.indices) {
            addNode(nodes, node.childrenNodes[i], currentLevel + 1)
        }
    }

    /**
     * 获取所有的根节点
     */
    private fun getRootNodes(nodes: MutableList<Node>): MutableList<Node> {
        val rootNodes: MutableList<Node> = ArrayList()
        for (node in nodes) {
            if (node.isRoot) {
                rootNodes.add(node)
            }
        }
        return rootNodes
    }

    /**
     * 将Node的parent和children的数据填充
     */
    private fun buildTreeNodes(nodes: MutableList<Node>): MutableList<Node> {
        /**
         * 比较nodes中的所有节点，分别添加children和parent
         */
        for (i in nodes.indices) {
            val n = nodes[i]
            for (j in i + 1 until nodes.size) {
                val m = nodes[j]
                Timber.i("nid:"+n.id+" m parent id:"+m.getparentId())
                Timber.i("n parent id:"+n.getparentId()+" m id:"+m.id)
                if (n.id == m.getparentId()) {
                    n.childrenNodes.add(m)
                    m.parent = n
                } else if (n.getparentId() == m.id) {
                    n.parent = m
                    m.childrenNodes.add(n)
                }
            }
        }
        return nodes
    }

    fun setNodeChecked(node: Node, isChecked: Boolean) {
        // 自己设置是否选择
        node.isChecked = isChecked
        node.task.isCompleted = isChecked
        /**
         * 非叶子节点,子节点处理
         */
        setChildrenNodeChecked(node, isChecked)
        /** 父节点处理  */
//        setParentNodeChecked(node)
    }

    /**
     * 非叶子节点,子节点处理
     */
    private fun setChildrenNodeChecked(node: Node, isChecked: Boolean) {
        node.isChecked = isChecked
        node.task.isCompleted = isChecked
        if (!node.isLeaf) {
            for (n in node.childrenNodes) {
                // 所有子节点设置是否选择
                setChildrenNodeChecked(n, isChecked)
            }
        }
    }

    /**
     * 设置父节点选择
     *
     * @param node
     */
    private fun setParentNodeChecked(node: Node) {
        /** 非根节点  */
        if (!node.isRoot) {
            val rootNode = node.parent
            var isAllChecked = true
            for (n in rootNode.childrenNodes) {
                if (!n.isChecked) {
                    isAllChecked = false
                    break
                }
            }
            rootNode.isChecked = isAllChecked
            setParentNodeChecked(rootNode)
        }
    }
}