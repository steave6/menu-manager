(function() {
'use strict';
var GalleryComposite = function (heading, id) {
    this.children = [];
    this.element = $('<div class="composite-gallery"></div>').attr('id', id)
        .append('<h2>' + heading + '</h2>');
}
GalleryComposite.prototype = {
    add: function (child) {
        this.children.push(child);
        this.element.append(child.getElement());
    },
    remove: function (child) {
        for (var node, i = 0; node = this.getChild(i); i++) {
            if (node == child) {
                this.children.splice(i, 1);
                this.element.detach(child.getElement());
                return true;
            }
            if (node.remove(child)) {
                return true;
            }
        }
        return false;
    },
    getChild: function (i) {
        return this.children[i];
    },
    getChildren: function () {
        return this.children;
    },
    hide: function () {
        for (var node, i = 0; node = this.getChild(i); i++) {
            node.hide();
        }
        this.element.hide(0);
    },
    show: function () {
        for (var node, i = 0; node = this.getChild(i); i++) {
            node.show();
        }
        this.element.show(0);
    },
    getElement: function () {
        return this.element;
    },
    getLeaf: function () {
        var leaf = [];
        for (i = 0; i < this.children.length; i++) {
            var node = getChild(i);
            if (node instanceof GalleryComposite) {
                leaf.concat(getLeaf());
            } else {
                leaf.push(node.getChild());
            }
        }
        return leaf;
    }
}

class GalleryTable {
  constructor (heading, id) {
    this.child = null;
    this.element = $('<div class="gallery-table"></div>').attr('id', id)
                    .append('<p>' + heading + '</p>');
  }
  add (data) {
      var container = document.getElementById(this.element.attr('id'));
      this.child = new handsonMenu.createTable(container, data);
  }
  remove () { }
  getChild () { return this.child }
  hide () {
      this.element.hide(0);
  }
  show () {
      this.element.show(0);
  }
  getElement () {
      return this.element;
  }

}

//var GalleryTable = function (id) {
//    this.child = null;
//    this.element = $('<div class="gallery-table"></div>')
//        .attr('id', id);
//}
//GalleryTable.prototype = {
//    // Due to this being a leaf, it doesn't use these methods,
//    // but must implement them to count as implementing the
//    // Composite interface
//    add: function (data) {
//        var container = document.getElementById(this.element.attr('id'));
//        this.child = new handsonMenu.createTable(container, data);
//    },
//    remove: function () { },
//    getChild: function () { return this.child },
//    hide: function () {
//        this.element.hide(0);
//    },
//    show: function () {
//        this.element.show(0);
//    },
//    getElement: function () {
//        return this.element;
//    }
//}

var fieldV = {
  topComp: null,
  hotMap: new Map(),
  recipeMap: new Map()
}

var mainMenu = {

    init: function () {
        this.getMenuList();
        this.setBasicDiv();
        this.setTable();
    },
    setBasicDiv: function() {
        var today = new Date();
        fieldV.topComp = new GalleryComposite('', 'toplevel-composite');
        for (var i=0; i < 7; i++) {
            var cdate = new Date();
            cdate.setDate(today.getDate() + Number(i));
            var cdatestr = cdate.getFullYear() + "-" + cdate.getMonth() + "-" + cdate.getDate();

            var secondlevel = new GalleryComposite(cdatestr, cdatestr);
            fieldV.topComp.add(secondlevel);
        }
        fieldV.topComp.getElement().appendTo('#myGrid');
    },
    setTable: function() {
        var comps = fieldV.topComp.getChildren();
        for (var i = 0; i < comps.length; i++) {
            var comp = comps[i];

            handsonMenu.getMenuBridge(comp);
        }
    },
    getMenuList: function() {
        handsonMenu.getMenuListSync(function(response, dataType) {
            var menu = [];
            for(var item in response){
                menu.push(response[item].name)
            }
            handsonMenu.menu = response;
        });
    }
}

var handsonMenu = {
  startRow: 0,
  endRow: 0,
  startCol: 0,
  endCol: 0,
  instance: null,
  menu: null,
  createTable: function (container, data) {
      var ht = new Handsontable(container, {
          data: data,
          currentRowClassName: 'currentRow',
          currentColClassName: 'currentCol',
          colHeaders: ["code", "メニュー"],
          rowHeaders: false,
          columns: [{
                  data: 'code',
                  width: '200px',
                  renderer: handsonMenu.Event.codeMenuRenderer
              }
          ],
          minRows: 7,
          maxRows: 7,
          contextMenu: true,
          afterSelectionEnd: handsonMenu.Event.afterSelectionEnd
      });
      fieldV.hotMap.set(container.id, ht);
      return ht;
  },
  getMenuBridge: function (composite) {
      handsonMenu.getMenuData('someidstring', function (response, dataType) {
          var item = ["one", "two", "three"]
          for (var i = 0; i < 3; i++) {
              var idstr = composite.getElement().attr('id') + "-" + (i+1)
              var gtable = new GalleryTable('', idstr)
              composite.add(gtable)

              gtable.add(response[item[i]])
          }
      });
  },
  getMenuData: function(id, callback) {
      $.ajax({
          url:'/api/get/mealtype', // + id,
          success: callback
      });
  },
  getMenuListBridge: function(id) {
      handsonMenu.getMenuList(function(response, dataType) {
          $.each(response, function(i, obj) {
              var $option = $('<option>')
                  .val(obj.code)
                  .text(obj.name);
              $(id).append($option);
              fieldV.recipeMap.set(String(obj.code), obj.name);
          });
      });
  },
  getMenuList: function(callback) {
      $.ajax({
          url: '/api/get/menulist',
          success: callback
      });
  },
  getMenuListSync: function (callback) {
      $.ajax({
          url: '/api/get/menulist',
          async: false,
          success: callback
      });
  },
  Event: {
    afterSelectionEnd: function (r, c, r2, c2) {
      handsonMenu.startRow = r;
      handsonMenu.endRow = r2;
      handsonMenu.startCol = c;
      handsonMenu.endCol = c2;
      handsonMenu.instance = this;
    },
    codeMenuRenderer: function (hotInstance, TD, row, col, prop, value, cellProperties) {
      var rmap = fieldV.recipeMap;
      var key = value + '';
      if (rmap.get(key) !== undefined) {
        TD.style.color = 'blue';
        TD.innerHTML = rmap.get(key);
      } else if (value === '' || value === null) {
        TD.innerHTML = '';
      } else {
        TD.style.backgroundColor = 'red';
        TD.innerHTML = value;
      }
    }
  },
  getInstance: function () {
      return handsonMenu.instance;
  },
  allRerender: function () {
    var hmap = fieldV.hotMap;
    hmap.forEach(function (value, key, map) {
      value.render();
    });
  }
}

var menuLists = {
    init: function() {
        var M = $('#menu')
        handsonMenu.getMenuListBridge(M)

        $('#menu').dblclick(function(data) {
            var selectTable = handsonMenu.getInstance(),
                srow = handsonMenu.startRow,
                scol = handsonMenu.startCol,
                erow = handsonMenu.endRow,
                ecol = handsonMenu.endCol;

            var smenu = $('#menu option:selected').text()
            selectTable.setDataAtCell(srow, scol, smenu)
        })
    }
}

$( document ).ready(function () {
    mainMenu.init();
    menuLists.init();

    setTimeout(handsonMenu.allRerender, 1000);
});

})();
