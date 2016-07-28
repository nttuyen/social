/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.image;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import junit.framework.TestCase;
import org.exoplatform.social.core.model.AvatarAttachment;

public class ImageUtilsTest extends TestCase {

  public void testBuildFileName() {
    String oldFileName = "avatar";
    String extendsion = ".jpg";
    String subfix = "RESIZED_";
    String postfix = "_100x100";
    String newFileName = ImageUtils.buildFileName(oldFileName + extendsion, subfix, postfix);
    assertEquals("Should be " + subfix + oldFileName + postfix + extendsion, subfix + oldFileName
        + postfix + extendsion, newFileName);
  }

  public void testBuildImagePostfix() {
    String postfix = ImageUtils.buildImagePostfix(100, -10);
    assertEquals("_100x0", postfix);
  }

  public void testCreateResizedAvatarAttachment(){
      InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
      int width = 200;
      String avatarId = "null";
      String avatarMimeType = "image/jpeg";
      String avatarFileName = "eXo-Social.png";
      String avatarWorkspace = "null";
      BufferedImage image = ImageUtils.image;
      try {
          image = ImageIO.read(inputStream);
      } catch (IOException e) {
          e.printStackTrace();
      }
      int height = image.getHeight() * width / image.getWidth();
      image = org.apache.shindig.gadgets.rewrite.image.ImageUtils.getScaledInstance(image, width, height, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false, BufferedImage.TYPE_INT_RGB);
      int avatarWidth = image.getWidth();
      int avatarHeight = image.getHeight();
      AvatarAttachment avatar = ImageUtils.createResizedAvatarAttachment(inputStream, width, height, avatarId, avatarFileName, avatarMimeType, avatarWorkspace);
      assertNotNull(avatar);
      assertEquals(avatarWidth, width);
      assertEquals(avatarHeight, height);
  }

}
