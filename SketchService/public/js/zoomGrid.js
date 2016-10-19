$(function () {
  var canvas = $('canvas.main').get(0)
  var canvasContext = canvas.getContext('2d')

  var ratio = 1
  var vpx = 0
  var vpy = 0
  var vpw = window.innerWidth
  var vph = window.innerHeight

  var orig_width = 4000
  var orig_height = 4000

  var width = 4000
  var height = 4000

  $(canvas).on('wheel', function (ev) {
    ev.preventDefault() // for stackoverflow

    var step

    if (ev.originalEvent.wheelDelta) {
      step = (ev.originalEvent.wheelDelta > 0) ? 0.05 : -0.05
    }

    if (ev.originalEvent.deltaY) {
      step = (ev.originalEvent.deltaY > 0) ? 0.05 : -0.05
    }

    if (!step) return false // yea..

    var new_ratio = ratio + step
    var min_ratio = Math.max(vpw / orig_width, vph / orig_height)
    var max_ratio = 3.0

    if (new_ratio < min_ratio) {
      new_ratio = min_ratio
    }

    if (new_ratio > max_ratio) {
      new_ratio = max_ratio
    }

    // zoom center point
    var targetX = ev.originalEvent.clientX || (vpw / 2)
    var targetY = ev.originalEvent.clientY || (vph / 2)

    // percentages from side
    var pX = ((vpx * -1) + targetX) * 100 / width
    var pY = ((vpy * -1) + targetY) * 100 / height

    // update ratio and dimentsions
    ratio = new_ratio
    width = orig_width * new_ratio
    height = orig_height * new_ratio

    // translate view back to center point
    var x = ((width * pX / 100) - targetX)
    var y = ((height * pY / 100) - targetY)

    // don't let viewport go over edges
    if (x < 0) {
      x = 0
    }

    if (x + vpw > width) {
      x = width - vpw
    }

    if (y < 0) {
      y = 0
    }

    if (y + vph > height) {
      y = height - vph
    }

    vpx = x * -1
    vpy = y * -1
  })

  var is_down, is_drag, last_drag

  $(canvas).css({ position: 'absolute', top: 0, left: 0 }).appendTo(document.body)

  function animate () {
    window.requestAnimationFrame(animate)

    canvasContext.clearRect(0, 0, canvas.width, canvas.height)

    canvasContext.lineWidth = 1
    canvasContext.strokeStyle = '#ccc'

    var step = 100 * ratio

    for (var x = vpx; x < width + vpx; x += step) {
      canvasContext.beginPath()
      canvasContext.moveTo(x, vpy)
      canvasContext.lineTo(x, vpy + height)
      canvasContext.stroke()
    }
    for (var y = vpy; y < height + vpy; y += step) {
      canvasContext.beginPath()
      canvasContext.moveTo(vpx, y)
      canvasContext.lineTo(vpx + width, y)
      canvasContext.stroke()
    }

    canvasContext.strokeRect(vpx, vpy, width, height)

    canvasContext.beginPath()
    canvasContext.moveTo(vpx, vpy)
    canvasContext.lineTo(vpx + width, vpy + height)
    canvasContext.stroke()

    canvasContext.beginPath()
    canvasContext.moveTo(vpx + width, vpy)
    canvasContext.lineTo(vpx, vpy + height)
    canvasContext.stroke()

    canvasContext.restore()
  }

  animate()
})