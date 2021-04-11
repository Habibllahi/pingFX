/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.entity;

import com.hicmikrolab.hotelManagementSystem.utility.NodeState;
import com.hicmikrolab.hotelManagementSystem.utility.NodeStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * This class by ORM technology represent a table in the database with each of its field serving as Column.
 * An instance of it represent a row in the database
 */
@Entity
@Data
@Table(name = "node",schema = "hicmikrolab")
public class Node {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;

    @Column(name = "node_name")
    String nodeName;

    @Column(name = "ip_address")
    String ipAddress;

    @Column(name = "socket_port")
    int socketPort;

    @Column(name = "node_position")
    String position;

    @Column(name = "node_status")
    @Enumerated(value = EnumType.STRING)
    NodeStatus nodeStatus;

    @Column(name = "node_state")
    @Enumerated(value = EnumType.STRING)
    NodeState nodeState;

    @Column(name = "is_deleted")
    boolean deleted;
}
