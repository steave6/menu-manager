(function() {
'use strict';

class GalleryComposite {
  constructor (heading, id) {
    this.children = [];
    this.element = $('<div class="composite-gallery"></div>').attr('id', id)
        .append('<h2>' + heading + '</h2>');
  }
  add (child) {
      this.children.push(child);
      this.element.append(child.getElement());
  }
  remove (child) {
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
  }
  getChild (i) {
      return this.children[i];
  }
  getChildren () {
      return this.children;
  }
  hide () {
      for (var node, i = 0; node = this.getChild(i); i++) {
          node.hide();
      }
      this.element.hide(0);
  }
  show () {
      for (var node, i = 0; node = this.getChild(i); i++) {
          node.show();
      }
      this.element.show(0);
  }
  getElement () {
      return this.element;
  }
  getLeaf () {
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
  constructor (id) {
    this.child = null;
    this.element = $('<div class="gallery-table"></div>').attr('id', id)
  }
  add (data) {
      var id = this.element.attr('id');
      this.child = new hotMenu.createTable(id, data);
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

var Field = {
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
        Field.topComp = new GalleryComposite('', 'toplevel-composite');
        for (var i=0; i < 7; i++) {
            var cdate = new Date();
            cdate.setDate(today.getDate() + Number(i));
            var cdatestr = cdate.getFullYear() + "-" + cdate.getMonth() + "-" + cdate.getDate();

            var secondlevel = new GalleryComposite(cdatestr, cdatestr);
            Field.topComp.add(secondlevel);
        }
        Field.topComp.getElement().appendTo('#myGrid');
    },
    setTable: function() {
        var comps = Field.topComp.getChildren();
        for (var i = 0; i < comps.length; i++) {
            var comp = comps[i];

            hotMenu.getMenuBridge(comp);
        }
    },
    getMenuList: function() {
        Ajax.getRecipeList(function(response, dataType) {
            var menu = [];
            for(var item in response){
                menu.push(response[item].name)
            }
            hotMenu.menu = response;
        });
    }
}

var hotMenu = {
  sTable: {
    startRow: 0,
    endRow: 0,
    startCol: 0,
    endCol: 0,
    instance: null
  },
  menu: null,
  createTable: function (id, data) {
    var container = document.getElementById(id);
    var ht = new Handsontable(container, {
      data: data,
      currentRowClassName: 'currentRow',
      currentColClassName: 'currentCol',
      colHeaders: ["code", "メニュー"],
      rowHeaders: false,
      columns: [{
          data: 'code',
          width: '200px',
          renderer: hotMenu.Event.codeMenuRenderer
        }
      ],
      minRows: 7,
      maxRows: 7,
      with: 200,
      height: 200,
      contextMenu: true,
      afterSelectionEnd: hotMenu.Event.afterSelectionEnd
    });
    Field.hotMap.set(id, ht);
    return ht;
  },
  getMenuBridge: function (composite) {
      Ajax.getMenuData('someidstring', function (response, dataType) {
          var item = ["one", "two", "three"]
          for (var i = 0; i < 3; i++) {
              var idstr = composite.getElement().attr('id') + "-" + (i+1)
              var gtable = new GalleryTable(idstr);
              composite.add(gtable)

              gtable.add(response[item[i]])
          }
      });
  },
  Event: {
    afterSelectionEnd: function (r, c, r2, c2) {
      hotMenu.sTable.startRow = r;
      hotMenu.sTable.endRow = r2;
      hotMenu.sTable.startCol = c;
      hotMenu.sTable.endCol = c2;
      hotMenu.sTable.instance = this;
    },
    codeMenuRenderer: function (hotInstance, TD, row, col, prop, value, cellProperties) {
      var rmap = Field.recipeMap;
      var key = value + '';
      var rname = rmap.get(key);

      if (rname !== undefined) {
        TD.style.color = 'R' !== key.substr(0,1) ? 'blue' : 'green';
        TD.innerText = rname;
      } else if (value === '' || value === null) {
        TD.innerText = '';
      } else {
        TD.style.backgroundColor = 'red';
        TD.innerText = value;
      }
    }
  },
  getInstance: function () {
      return hotMenu.sTable.instance;
  },
  allRerender: function () {
    var hmap = Field.hotMap;
    hmap.forEach(function (value, key, map) {
      value.render();
    });
  }
}

var Ajax = {
  getMenuData: function(id, callback) {
    $.ajax({
      url:'/api/get/mealtype', // + id,
      success: callback
    });
  },
  getRecipeList: function(callback) {
    $.ajax({
      url: '/api/get/menulist',
      success: callback
    });
  }
}

var hotRecipe = {
  instance: null,

  init: function() {
    hotRecipe.getMenuListBridge('recipeGrid')

    $('#menu').dblclick(function(data) {
      var ins = hotMenu.sTable;
      var selectTable = hotMenu.getInstance(),
          srow = ins.startRow,
          scol = ins.startCol,
          erow = ins.endRow,
          ecol = ins.endCol;

      var smenu = $('#menu option:selected').text()
      selectTable.setDataAtCell(srow, scol, smenu)
    })
  },
  getMenuListBridge: function(id) {
    Ajax.getRecipeList(function(response, dataType) {
      hotRecipe.createTable(id, response);

      for (var item in response) {
        Field.recipeMap.set(response[item].code + '', response[item].name);
      }
    });
  },
  createTable: function (id, data) {
    var container = document.getElementById(id);
    var hot = new Handsontable(container, {
      data: data,
      colHeaders: ["code", "メニュー"],
      rowHeaders: false,
      columns: [{
          data: 'code',
          width: '100px',
          readOnly: true
//          onCellDblClick: hotRecipe.Event.onCellDblClick
        },
        {
          data: 'name',
          width: '200px',
          readOnly: true
        }
      ],
      minRows: 7,
      height: 500,
      stretchH: 'last',
      contextMenu: true
    });
    hot.view.wt.update('onCellDblClick', hotRecipe.Event.onCellDblClick);

    hotRecipe.instance = hot;
  },
  Event: {
    onCellDblClick: function (mouseEv, rowcol, targetEl, myself) {
      var sdata = hotRecipe.instance.getDataAtCell(rowcol.row, rowcol.col);
      console.log(sdata)
      var hmenu = hotMenu.sTable;
      hmenu.instance.setDataAtCell(hmenu.startRow, hmenu.startCol, sdata);
    }
  }
}

$( document ).ready(function () {
  mainMenu.init();
  hotRecipe.init();

  setTimeout(hotMenu.allRerender, 1500);
});

})();
