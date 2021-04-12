/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.service;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import com.hicmikrolab.hotelManagementSystem.repository.NodeRepoI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppServiceImpl implements AppServiceI{

    @Autowired
    NodeRepoI nodeRepoI;
    @Override
    public List<Node> findAllNode() {
        return nodeRepoI.findAll();
    }

    @Override
    public Node saveNode(Node node) {
        return nodeRepoI.save(node);
    }

    @Override
    public Node updateNode(Node node) {
        var existingNode = nodeRepoI.findById(node.getId());
        return existingNode.map(closureNode -> {
            closureNode.setNodeName(node.getNodeName());
            closureNode.setIpAddress(node.getIpAddress());
            closureNode.setSocketPort(node.getSocketPort());
            closureNode.setPosition(node.getPosition());
            closureNode.setDeleted(node.isDeleted());
            return saveNode(closureNode);
        }).orElseGet(()-> saveNode(node));
    }

    @Override
    public Node onlyUpdateNodeStatus(Node node) {
        var existingNode = nodeRepoI.findById(node.getId());
        return existingNode.map(closureNode -> {
            closureNode.setNodeStatus(node.getNodeStatus());
            return saveNode(closureNode);
        }).orElseGet(()-> saveNode(node));
    }

    @Override
    public Node onlyUpdateNodeState(Node node) {
        var existingNode = nodeRepoI.findById(node.getId());
        return existingNode.map(closureNode -> {
            closureNode.setNodeState(node.getNodeState());
            return saveNode(closureNode);
        }).orElseGet(()-> saveNode(node));
    }

    @Override
    public Node updateStateAndStatus(Node node) {
        var existingNode = nodeRepoI.findById(node.getId());
        return existingNode.map(closureNode -> {
            closureNode.setNodeState(node.getNodeState());
            closureNode.setNodeStatus(node.getNodeStatus());
            return saveNode(closureNode);
        }).orElseGet(()-> saveNode(node));
    }

}
