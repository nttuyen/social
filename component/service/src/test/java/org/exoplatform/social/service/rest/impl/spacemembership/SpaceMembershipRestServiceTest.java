package org.exoplatform.social.service.rest.impl.spacemembership;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.ArrayUtils;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.impl.DefaultSpaceApplicationHandler;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.service.rest.RestProperties;
import org.exoplatform.social.service.rest.api.models.SpaceMembershipsCollections;
import org.exoplatform.social.service.test.AbstractResourceTest;

public class SpaceMembershipRestServiceTest extends AbstractResourceTest {
  private IdentityManager identityManager;
  private SpaceService spaceService;
  
  private SpaceMembershipRestServiceV1 spaceMembershipRestService;
  
  private List<Space> tearDownSpaceList;
  
  private Identity rootIdentity;
  private Identity johnIdentity;
  private Identity maryIdentity;
  private Identity demoIdentity;

  public void setUp() throws Exception {
    super.setUp();
    
    System.setProperty("gatein.email.domain.url", "localhost:8080");
    tearDownSpaceList = new ArrayList<Space>();
    
    identityManager = (IdentityManager) getContainer().getComponentInstanceOfType(IdentityManager.class);
    spaceService = (SpaceService) getContainer().getComponentInstanceOfType(SpaceService.class);
    
    rootIdentity = identityManager.getOrCreateIdentity("organization", "root", true);
    johnIdentity = identityManager.getOrCreateIdentity("organization", "john", true);
    maryIdentity = identityManager.getOrCreateIdentity("organization", "mary", true);
    demoIdentity = identityManager.getOrCreateIdentity("organization", "demo", true);
    
    spaceMembershipRestService = new SpaceMembershipRestServiceV1();
    registry(spaceMembershipRestService);
  }

  public void tearDown() throws Exception {
    for (Space space : tearDownSpaceList) {
      Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);
      if (spaceIdentity != null) {
        identityManager.deleteIdentity(spaceIdentity);
      }
      spaceService.deleteSpace(space);
    }
    
    identityManager.deleteIdentity(rootIdentity);
    identityManager.deleteIdentity(johnIdentity);
    identityManager.deleteIdentity(maryIdentity);
    identityManager.deleteIdentity(demoIdentity);
    
    super.tearDown();
    unregistry(spaceMembershipRestService);
  }

  public void testGetSpaceMembersShip() throws Exception {
    //root creates 2 spaces, john 1 and mary 1
    getSpaceInstance(1, "root");
    getSpaceInstance(2, "root");
    getSpaceInstance(3, "john");
    getSpaceInstance(4, "mary");
    
    startSessionAs("root");
    ContainerResponse response = service("GET", "/v1/social/spacesMemberships", "", null, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    SpaceMembershipsCollections collections = (SpaceMembershipsCollections) response.getEntity();
    assertEquals(8, collections.getSpaceMemberships().size());
    
    response = service("GET", "/v1/social/spacesMemberships?user=root", "", null, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    collections = (SpaceMembershipsCollections) response.getEntity();
    assertEquals(4, collections.getSpaceMemberships().size());
    
    response = service("GET", "/v1/social/spacesMemberships?space=space3", "", null, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    collections = (SpaceMembershipsCollections) response.getEntity();
    assertEquals(2, collections.getSpaceMemberships().size());
  }
  
  public void testAddSpaceMemberShip() throws Exception {
    //root creates 1 space
    Space space = getSpaceInstance(1, "root");
    
    //root add demo as member of his space
    startSessionAs("root");
    String input = "{\"space\":space1, \"user\":demo}";
    ContainerResponse response = getResponse("POST", "/v1/social/spacesMemberships", input);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    
    space = spaceService.getSpaceById(space.getId());
    assertTrue(ArrayUtils.contains(space.getMembers(), "demo"));
    
    //demo add mary as member of space1 but has no permission
    startSessionAs("demo");
    input = "{\"space\":space1, \"user\":mary}";
    response = getResponse("POST", "/v1/social/spacesMemberships", input);
    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
  }
  
  public void testGetUpdateDeleteSpaceMembership() throws Exception {
    //root creates 1 space
    getSpaceInstance(1, "root");
    
    //root add demo as member of his space
    startSessionAs("root");
    String id = "space1:root:member";
    ContainerResponse response = service("GET", "/v1/social/spacesMemberships/" + id, "", null, null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    Map<String, Object> result = (Map<String, Object>) response.getEntity();
    assertEquals("member", result.get(RestProperties.ROLE));
    
  }
  
  private Space getSpaceInstance(int number, String creator) throws Exception {
    Space space = new Space();
    space.setDisplayName("space" + number);
    space.setPrettyName(space.getDisplayName());
    space.setRegistration(Space.OPEN);
    space.setDescription("add new space " + number);
    space.setType(DefaultSpaceApplicationHandler.NAME);
    space.setVisibility(Space.PRIVATE);
    space.setRegistration(Space.VALIDATION);
    space.setPriority(Space.INTERMEDIATE_PRIORITY);
    space.setGroupId("/space/space" + number);
    String[] managers = new String[] {creator};
    String[] members = new String[] {creator};
    space.setManagers(managers);
    space.setMembers(members);
    space.setUrl(space.getPrettyName());
    this.spaceService.saveSpace(space, true);
    tearDownSpaceList.add(space);
    return space;
  }
}
