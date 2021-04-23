/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.repository;

import com.hicmikrolab.hotelManagementSystem.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NodeRepoI extends JpaRepository<Node, UUID> {

/*    @Query(value = "select * from node where is_deleted=false",nativeQuery = true)
    List<Node> findUndeletedNodes();*/

}
