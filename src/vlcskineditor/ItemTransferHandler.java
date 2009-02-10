/*****************************************************************************
 * ListTransferHandler.java
 *****************************************************************************
 * Copyright (C) 2009 Daniel Dreibrodt
 *
 * This file is part of VLC Skin Editor
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package vlcskineditor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import vlcskineditor.items.*;

/**
 * Drag and Drop support of Items
 * @author Daniel Dreibrodt
 */
public class ItemTransferHandler extends TransferHandler {

  private Skin s;
  private DataFlavor itemFlavor = new DataFlavor(Item.class, "Skin item");

  public ItemTransferHandler(Skin s) {
    this.s = s;
  }

  private class ItemTransferer implements Transferable {

    private Item i;

    public ItemTransferer(Item i_) {
      i = i_;
    }

    public Item getItem() {
      return i;
    }

    public DataFlavor[] getTransferDataFlavors() {
      DataFlavor[] availableFlavors = { itemFlavor };
      return availableFlavors;
    }
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor==itemFlavor;
    }
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if(isDataFlavorSupported(flavor)) {
        return i;
      } else throw new UnsupportedFlavorException(flavor);
    }
    
  }

  @Override
  public int getSourceActions(JComponent c) {
    return MOVE;
  }

  @Override
  public boolean canImport(TransferSupport support) {
    if(support.isDataFlavorSupported(itemFlavor)) {
      try {
        Item i = (Item)support.getTransferable().getTransferData(itemFlavor);

        DropLocation dl = support.getDropLocation();
        TreePath tp = ((JTree)support.getComponent()).getPathForLocation(dl.getDropPoint().x, dl.getDropPoint().y);

        String target_item_id = tp.getLastPathComponent().toString();
        target_item_id = target_item_id.substring(target_item_id.indexOf(": ")+2);

        if(i.id.equals(target_item_id)) return false;
        
        if(i.getClass().equals(Slider.class)) {
          if( ((Slider)i).isInPlaytree()) return false;
          else return true;
        } else if(i.getClass().equals(SliderBackground.class)) return false;
        else return true;
      } catch(Exception ex) {
        return false;
      }
    } else return false;
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    JTree jt = (JTree)c;
    String selection = jt.getSelectionPath().getLastPathComponent().toString();
    String selected_item = selection.substring(selection.indexOf(": ")+2);
    Item i = s.getItem(selected_item);
    return new ItemTransferer(i);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if(!support.isDrop()) return false;
    JTree jt = (JTree)support.getComponent();
    DropLocation dl = support.getDropLocation();
    TreePath tp = jt.getPathForLocation(dl.getDropPoint().x, dl.getDropPoint().y);
    if(tp.getPathCount()==1) {
      return false;
    } else {
      String target_item_id = tp.getLastPathComponent().toString();
      target_item_id = target_item_id.substring(target_item_id.indexOf(": ")+2);
      Item target_item = s.getItem(target_item_id);
      Item source_item = null;
      try {
        source_item = (Item)support.getTransferable().getTransferData(itemFlavor);
      } catch(Exception ex) {
        ex.printStackTrace();
        return false;
      }
      List<Item> source_list = s.getParentListOf(source_item.id);
      source_list.remove(source_item);
      if(target_item.getClass().equals(Panel.class)) {
        ((Panel)target_item).items.add(source_item);     
      } else {
        List<Item> list =  s.getParentListOf(target_item_id);
        list.add(list.indexOf(target_item),source_item);
      }

      //Update the tree first after the drop action has been finished
      //Otherwise there will occur a NullPointerException in the drawing of the JTree
      new Thread(source_item.id) {

        private String id;

        private Thread setID(String id_) {
          id = id_;
          return this;
        }

        @Override
        public void run() {
          try {
            sleep(10);
            s.updateItems();
            s.expandItem(id);
          } catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      }.setID(source_item.id).start();

      return true;
    }    
  }




}
