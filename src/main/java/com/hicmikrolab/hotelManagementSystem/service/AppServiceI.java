/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.service;

import com.hicmikrolab.hotelManagementSystem.entity.Node;

import java.util.List;

public interface AppServiceI {
    List<Node> findAllNode();
    Node saveNode(Node node);
    Node updateNode(Node node);
    Node onlyUpdateNodeStatus(Node node);
    Node onlyUpdateNodeState(Node node);
    Node updateStateAndStatus(Node node);

}
