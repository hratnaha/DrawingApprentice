//version: 072213
var SketchDataStructure = {
  pac: function(data, type, para) {
    var d = new Date();
    var t = type===undefined?'data':type;
    var p = para===undefined?'':para;
    return {
      'type': t,
      'para': p,
      'time': d.toISOString(),
      'data': data
    };
  },
  network: {
    serverPorts: {
      webSocketsServerPort: 21073,
    },
    connection: {
      //conn: can be socket or WebSocketConnection
      conn:'',
      //type: ws or sock
      type:''
    },
    protocols: {

    },
    pac: {
      type: {cmd:'cmd', stroke:'stroke'},
      para_cmd: {analytics:'analytics', data:'data', visualization:'visualization', admin:'admin'},
      para_data: {event:'event', dataSource:'dataSource'}
    }
  },
  commands: {
    sketch: [
      { cmd: '', type: '', para: Array }, // template
    ]
  },
  error: [
    { name:'', type:'', desc:'' } // template
  ],
  dataFormat: {
    mouseEvent: {
      type: String,
      shiftKey: Boolean,
      altKey: Boolean,
      ctrlKey: Boolean,
      button: Number,
      clientX: Number,
      clientY: Number,
      screenX: Number,
      screenY: Number
    },
    keyboardEvent: {
      shiftKey: Boolean,
      altKey: Boolean,
      ctrlKey: Boolean,
      keyCode: Number,
      charCode: Number,
      keyIdentifier: String,
      type: String
    },
    dataSource: {
      type:String,
      url:String,
      name:String
    }
  }
};
module.exports = SketchDataStructure;