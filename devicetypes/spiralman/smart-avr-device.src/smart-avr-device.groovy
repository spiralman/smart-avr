metadata {
  definition (name: "Smart AVR Device", namespace: "spiralman", author: "Thomas Stephens") {
    capability "Switch"
    capability "Refresh"
    capability "Media Controller"
    capability "Switch Level"

    command "sourceUp"
    command "sourceDown"

    command "sourceBD"
    command "sourceSATCBL"
    command "sourceTV"
    command "sourceNETUSB"
    command "sourceDVD"
    command "sourceVAUX"
    command "sourceDOCK"
    command "sourceTUNER"
    command "sourceGAME"
    command "sourceGAME2"
    command "sourceDVR"
    command "sourceCD"

    command "levelUp"
    command "levelDown"

    command "mute"
    command "unmute"
    attribute "mute", "string"

    command "tuneDown"
    command "tuneUp"
    attribute "tuneFreq", "number"

    command "tuneBandFM"
    command "tuneBandAM"
    attribute "tuneBand", "enum", ["AM", "FM"]

    command "tuneAutoOn"
    command "tuneAutoOff"
    attribute "tuneAutoMode", "enum", ["AUTO", "MANUAL"]
  }

  preferences {
    input "receiverIp", "string", title: "Receiver IP address",
               description: "IP Address of Receiver (not proxy)",
               required: true, displayDuringSetup: true
  }

  simulator {
    // TODO: define status and reply messages here
  }

  tiles(scale: 2) {
    multiAttributeTile(name: "dashboard", type: "generic", width: 6, height: 4) {
      tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        attributeState "off", label: '${name}', action: "switch.on", icon: "st.Electronics.electronics10", backgroundColor: '#ffffff'
        attributeState "on", label: '${name}', action: "switch.off", icon: "st.Electronics.electronics10", backgroundColor: '#79b821'
      }

      tileAttribute("device.currentActivity", key: "SECONDARY_CONTROL") {
        attributeState("default", label: 'Source: ${currentValue}')
      }

      tileAttribute("device.level", key: "VALUE_CONTROL") {
        attributeState "VALUE_UP", action: "levelUp"
        attributeState "VALUE_DOWN", action: "levelDown"
      }
    }

    standardTile("refresh", "device.switch", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }

    standardTile("sourceDown", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 2, height: 1) {
      state "default", label:"Prev Source", action:"sourceDown", icon:"st.thermostat.thermostat-left"
    }

    standardTile("sourceUp", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 2, height: 1) {
      state "default", label:"Next Source", action:"sourceUp", icon:"st.thermostat.thermostat-right"
    }

    standardTile("mute", "device.mute", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "muted", label: "Mute", action: "unmute", icon: "st.custom.sonos.muted"
      state "unmuted", label: "Mute", action: "mute", icon: "st.custom.sonos.unmuted"
    }

    standardTile("BD", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label:"BD", action:"sourceBD", icon:"st.Electronics.electronics9"
    }

    standardTile("SATCBL", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "SAT/CBL", action: "sourceSATCBL", icon: "st.Electronics.electronics18"
    }
    standardTile("TV", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "TV", action: "sourceTV", icon: "st.Electronics.electronics18"
    }
    standardTile("NETUSB", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "NET/USB", action: "sourceNETUSB", icon: "st.Entertainment.entertainment2"
    }
    standardTile("DVD", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "DVD", action: "sourceDVD", icon: "st.Electronics.electronics8"
    }
    standardTile("VAUX", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "VAUX", action: "sourceVAUX", icon: "st.Electronics.electronics6"
    }
    standardTile("DOCK", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "DOCK", action: "sourceDOCK", icon: "st.Entertainment.entertainment4"
    }
    standardTile("TUNER", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "TUNER", action: "sourceTUNER", icon: "st.Electronics.electronics10"
    }
    standardTile("GAME1", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "GAME1", action: "sourceGAME", icon: "st.Electronics.electronics5"
    }
    standardTile("GAME2", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "GAME2", action: "sourceGAME2", icon: "st.Electronics.electronics5"
    }
    standardTile("DVR", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "DVR", action: "sourceDVR", icon: "st.Electronics.electronics18"
    }
    standardTile("CD", "device.mediaController", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label: "CD", action: "sourceCD", icon: "st.Electronics.electronics1"
    }

    standardTile("tuneBand", "device.tuneBand", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "AM", label: "AM", action: "tuneBandFM", icon: "st.Electronics.electronics10"
      state "FM", label: "FM", action: "tuneBandAM", icon: "st.Electronics.electronics10"
    }

    standardTile("tuneDown", "device.tuneDown", inactiveLabel: false,
                 decoration: "flat") {
      state "default", action:"tuneDown", icon:"st.thermostat.thermostat-down"
    }

    valueTile("tuneFreq", "device.tuneFreq", decoration: "flat", width: 2) {
      state "default", label: '${currentValue} kHz'
    }

    standardTile("tuneUp", "device.tuneUp", inactiveLabel: false,
                 decoration: "flat") {
      state "default", action:"tuneUp", icon:"st.thermostat.thermostat-up"
    }

    standardTile("tuneAutoMode", "device.tuneAutoMode", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "AUTO", label: "Auto", action: "tuneAutoOff"
      state "MANUAL", label: "Manual", action: "tuneAutoOn"
    }

    main "dashboard"
    details(["dashboard", "refresh",
             "sourceDown", "sourceUp", "mute",
             "BD",
             "SATCBL",
             "TV",
             "NETUSB",
             "DVD",
             "VAUX",
             "DOCK",
             "TUNER",
             "GAME1",
             "GAME2",
             "DVR",
             "CD",
             "tuneBand", "tuneDown", "tuneFreq", "tuneUp"
            ])
  }
}

def getProxyIp() {
  getDataValue("ip")
}

def getProxyPort() {
  return getDataValue("port")
}

def _parsePW(line) {
  def avrState = line.substring(2)
  def switchState

  if (avrState == 'ON') {
    switchState = 'on'
  }
  else if (avrState == 'STANDBY') {
    switchState = 'off'
  }

  log.debug "The receiver is ${switchState}"
  return createEvent(name: 'switch', value: switchState)
}

def _parseSI(line) {
  def inputState = line.substring(2)
  log.debug "The receiver is on input ${inputState}"
  return createEvent(name: 'currentActivity', value: inputState)
}

def _parseMV(line) {
  def volText = line.substring(2, 4)

  def vol = volText.toFloat()

  if (line.size() == 5) {
    vol += 0.5
  }

  log.debug "Master volume is ${vol}"

  return createEvent(name: 'level', value: vol)
}

def _parseMU(line) {
  def avrState = line.substring(2)
  def muteState

  if (avrState == 'ON') {
    muteState = 'muted'
  }
  else if (avrState == 'OFF') {
    muteState = 'unmuted'
  }

  log.debug "The receiver is ${muteState}"
  return createEvent(name: 'mute', value: muteState)
}

def _parseTF(line) {
  def freqText = line.substring(4, 8) + "." + line.substring(8)
  def freq = freqText.toFloat()

  log.debug "Freq is ${freq}"
  return createEvent(name: 'tuneFreq', value: freq)
}

def _parseTM(line) {
  def band = line.substring(4)
  if (band in ['FM', 'AM']) {
    log.debug "Tuner on band ${band}"

    return createEvent(name: 'tuneBand', value: band)
  }
  else if (band in ['AUTO', 'MANUAL']) {
    log.debug "Tuner auto mode: ${band}"

    return createEvent(name: 'tuneAutoMode', value: band)
  }
}

// parse events into attributes
def parse(String description) {
  def msg = parseLanMessage(description)
  def events = []

  if (msg.status > 299) {
    log.error "Got error: ${msg.body}"
    throw new Exception()
  }

  if (msg.body == null) {
    log.debug "Empty message"
    return []
  }

  msg.body.eachLine { line ->
    if (line.startsWith('PW')) {
      events << _parsePW(line)
    }
    else if (line.startsWith('SI')) {
      events << _parseSI(line)
    }
    else if (line.startsWith('MV') && !line.startsWith('MVMAX')) {
      events << _parseMV(line)
    }
    else if (line.startsWith('MU')) {
      events << _parseMU(line)
    }
    else if (line.startsWith('TF')) {
      events << _parseTF(line)
    }
    else if (line.startsWith('TM')) {
      events << _parseTM(line)
    }
    else {
      log.debug "Unknown line: ${line}"
    }
  }

  return events
}

def sync(ip, port) {
  def existingIp = getDataValue("ip")
  def existingPort = getDataValue("port")
  if (ip && ip != existingIp) {
    updateDataValue("ip", ip)
  }
  if (port && port != existingPort) {
    updateDataValue("port", port)
  }
}

def _avrCommand(command) {
  def result = new physicalgraph.device.HubAction(method: "GET",
                                                  path: "/avr/command",
                                                  headers: [
                                                    HOST: getHostAddress()
                                                  ],
                                                  query: [cmd: command]
                                                 )
  return result
}

def on() {
  return _avrCommand("PWON")
}

def off() {
  return _avrCommand("PWSTANDBY")
}

def refresh() {
  getAllActivities()
  return [_avrCommand("PW?"),
          _avrCommand("MV?"),
          _avrCommand("MU?"),
          _avrCommand("TFAN?"),
          _avrCommand("TMAN?"),
          getCurrentActivity()]
}

def _sources() {
  def sources = device.currentValue("activities")
  sources = sources.substring(1, sources.size() - 1).split(', ')

  return sources
}

def _sourceIndex() {
  def currentSource = device.currentValue("currentActivity")
  def sources = _sources()

  def sourceIndex = sources.findIndexOf { it == currentSource }
  return sourceIndex
}

def sourceUp() {
  log.debug "Source up"
  def sources = _sources()
  def curIndex = _sourceIndex()

  log.debug "Switching from source ${curIndex}"

  if (curIndex == 0) {
    curIndex = sources.size() - 1
  }
  else {
    curIndex -= 1
  }

  log.debug "to source ${curIndex}"

  return startActivity(sources.getAt(curIndex))
}

def sourceDown() {
  log.debug "Source down"
  def sources = _sources()
  def curIndex = _sourceIndex()

  log.debug "Switching from source ${curIndex}"

  if (curIndex == sources.size() - 1) {
    curIndex = 0
  }
  else {
    curIndex += 1
  }

  log.debug "to source ${curIndex}"

  return startActivity(sources.getAt(curIndex))
}

def sourceBD() {
  startActivity("BD")
}
def sourceSATCBL() {
  startActivity("SAT/CBL")
}
def sourceTV() {
  startActivity("TV")
}
def sourceNETUSB() {
  startActivity("NET/USB")
}
def sourceDVD() {
  startActivity("DVD")
}
def sourceVAUX() {
  startActivity("V.AUX")
}
def sourceDOCK() {
  startActivity("DOCK")
}
def sourceTUNER() {
  startActivity("TUNER")
}
def sourceGAME() {
  startActivity("GAME")
}
def sourceGAME2() {
  startActivity("GAME2")
}
def sourceDVR() {
  startActivity("DVR")
}
def sourceCD() {
  startActivity("CD")
}

def startActivity(activity) {
  log.debug "Switching to source ${activity}"
  // AVR doesn't return current source when switching sources, so
  // force-refresh it
  sendEvent(name: 'currentActivity', value: activity)
  return _avrCommand("SI" + activity)
}

def getAllActivities() {
  sendEvent(name: 'activities', value:
            [
              "CD",
              "TUNER",
              "DVD",
              "BD",
              "TV",
              "SAT/CBL",
              "GAME",
              "GAME2",
              "V.AUX",
              "DOCK",
              "IPOD",
              "NET/USB",
              "RHAPSODY",
              "NAPSTER",
              "PANDORA",
              "LASTFM",
              "FLICKR",
              "FAVORITES",
              "IRADIO",
              "SERVER",
              "USB/IPOD",
              "USB",
              "IPD",
              "IRP",
              "FVP"
            ])
}

def getCurrentActivity() {
  return _avrCommand("SI?")
}

def setLevel(level) {
  log.debug "Level request for ${level}"
}

def levelUp() {
  return _avrCommand("MVUP")
}

def levelDown() {
  return _avrCommand("MVDOWN")
}

def mute() {
  return _avrCommand("MUON")
}

def unmute() {
  return _avrCommand("MUOFF")
}

def tuneUp() {
  return _avrCommand("TFANUP")
}

def tuneDown() {
    return _avrCommand("TFANDOWN")
}

def tuneBandAM() {
  return _avrCommand("TMANAM")
}

def tuneBandFM() {
  return _avrCommand("TMANFM")
}

def tuneAutoOn() {
  return _avrCommand("TMANAUTO")
}

def tuneAutoOff() {
  return _avrCommand("TMANMANUAL")
}

// Just copy pasted from SmartThings docs :-(
// gets the address of the device
private getHostAddress() {
  def ip = getDataValue("ip")
  def port = getDataValue("port")

  // def ip = 'C0A80168'
  // def port = '1388'

  if (!ip) {
    def parts = device.deviceNetworkId.split(":")
    if (parts.length == 2) {
      ip = parts[0]
      port = parts[1]
    } else {
      log.warn "Can't figure out ip and port for device: ${device.id}"
    }
  }

  log.debug "Using IP: $ip and port: $port for device: ${device.id}"
  return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
  return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
  return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
