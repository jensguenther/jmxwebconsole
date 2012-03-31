/**
 * 
 */
package controllers.api.v1.jmxconnection.domain;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import models.jstree.JsTreeNode;

import org.junit.Before;
import org.junit.Test;

import play.libs.Codec;
import controllers.api.v1.jmxconnection.domain.DomainResource.ObjectNameJsTreeNodeTreeBuilder;

/**
 * @author jguenther
 *
 */
public class DomainResourceObjectNameJsTreeNodeTreeBuilderTest {
	
	private final static String BASE_URL = "/baseurl/";
	
	private ObjectNameJsTreeNodeTreeBuilder builder;

	@Before
	public void before(){
		builder = new ObjectNameJsTreeNodeTreeBuilder(BASE_URL);
	}
	
	@Test
	public void testBuild_withTwoNodesSameId() throws MalformedObjectNameException {
		ObjectName oname1 = new ObjectName("domain:key=value");

		// when
		List<JsTreeNode> roots = builder
				.add(oname1)
				.add(oname1)
				.build();
		
		// then
		JsTreeNode node = roots.get(0);
		assertEquals(Codec.encodeBASE64("domain:key=value"), getId(node));
	}
	
	@Test
	public void testBuild_withTwoNodesDifferentId() throws MalformedObjectNameException {
		ObjectName oname1 = new ObjectName("domain:key=value1");
		ObjectName oname2 = new ObjectName("domain:key=value2");

		// when
		List<JsTreeNode> roots = builder
				.add(oname1)
				.add(oname2)
				.build();
		
		// then (node 1 check)
		JsTreeNode node1 = roots.get(0);
		assertEquals(Codec.encodeBASE64("domain:key=value1"), getId(node1));
		// and (node 2 check)
		JsTreeNode node2 = roots.get(1);
		assertEquals(Codec.encodeBASE64("domain:key=value2"), getId(node2));
	}
	
	@Test
	public void testBuild_withTwoNodesSameRoot() throws MalformedObjectNameException {
		ObjectName oname1 = new ObjectName("domain:key1=value1");
		ObjectName oname2 = new ObjectName("domain:key1=value1,key2=value2");
		
		// when
		List<JsTreeNode> roots = builder
				.add(oname1)
				.add(oname2)
				.build();
		
		// then (node1 level 1 check)
		JsTreeNode node1 = roots.get(0);;
		assertEquals(Codec.encodeBASE64("domain:key1=value1"), getId(node1));
		// and (node2 level 1 check)
		JsTreeNode containerNode21 = roots.get(1);
		assertEquals(Codec.encodeBASE64("domain:key1=value1"), getId(containerNode21));
		// and (node2 level 2 check)
		JsTreeNode node22 = containerNode21.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key2=value2"), getId(node22));
	}
	
	@Test
	public void testBuild_withTwoNodesSameLevel1SameLevel2() throws MalformedObjectNameException {
		ObjectName oname1 = new ObjectName("domain:key1=value1,key2=value2");

		// when
		List<JsTreeNode> roots = builder
				.add(oname1)
				.add(oname1)
				.build();
		
		// then (node level 1 check)
		JsTreeNode containerNode1 = roots.get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1"), getId(containerNode1));
		// and (node level 2 check)
		JsTreeNode node2 = containerNode1.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key2=value2"), getId(node2));
	}
	
	@Test
	public void testBuild_withThreeNodesSameLevel1DifferentLevel2DifferentLevel3() throws MalformedObjectNameException {
		ObjectName oname1 = new ObjectName("domain:key1=value1,key21=value21,key31=value31");
		ObjectName oname2 = new ObjectName("domain:key1=value1,key22=value22,key32=value32");
		ObjectName oname3 = new ObjectName("domain:key1=value1,key22=value22,key33=value33");
		
		// when
		List<JsTreeNode> roots = builder
				.add(oname1)
				.add(oname2)
				.add(oname3)
				.build();
		
		// then
		assertEquals(1, roots.size());
		// and (node level 1 check)
		JsTreeNode containerNode1 = roots.get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1"), getId(containerNode1));
		// and (node21 level 2 check)
		JsTreeNode containerNode21 = containerNode1.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key21=value21"), getId(containerNode21));
		// and (node 31 level 3 check)
		JsTreeNode node31 = containerNode21.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key21=value21,key31=value31"), getId(node31));
		
		// and (node22 level 2 check)
		JsTreeNode containerNode22 = containerNode1.getChildren().get(1);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key22=value22"), getId(containerNode22));
		// and (node 32 level 3 check)
		JsTreeNode node32 = containerNode22.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key22=value22,key32=value32"), getId(node32));
		// and (node 33 level 3 check)
		JsTreeNode node33 = containerNode22.getChildren().get(1);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key22=value22,key33=value33"), getId(node33));
	}
	
	// -------------------------------------------------------------------------------------------------------
	// convenience
	// -------------------------------------------------------------------------------------------------------

	private String getId(JsTreeNode node){
		return (String)node.getData().getAttr().get(JsTreeNode.ATTR_KEY_ID);
	}
}
