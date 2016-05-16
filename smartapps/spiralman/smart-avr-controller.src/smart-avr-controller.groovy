definition(
    name: "Smart AVR Controller",
    namespace: "spiralman",
    author: "Thomas Stephens",
    description: "An application for controlling Denon AVR receivers",
    category: "SmartThings Labs",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  page(
    name: "deviceDiscovery",
    title: "Smart AVR Device Setup",
    content: "deviceDiscovery")
}

def getSearchTarget() {
  return "urn:schemas-upnp-org:device:AVRProxy:1"
}

def deviceDiscovery() {
  def options = [:]
  def devices = getVerifiedDevices()
  devices.each {
    log.debug "Device info ${it.value}"
    def value = it.value.name ?: "UPnP Device ${it.value.ssdpUSN.split(':')[1][-3..-1]}"
    def key = it.value.mac
    options["${key}"] = value
  }

  ssdpSubscribe()

  ssdpDiscover()
  verifyDevices()

  return dynamicPage(name: "deviceDiscovery",
                     title: "Discovery Started!",
                     nextPage: "",
                     refreshInterval: 5,
                     install: true,
                     uninstall: true) {
    section("Please wait while we discover your UPnP Device. Discovery can take five minutes or more, so sit back and relax! Select your device below once discovered.") {
      input "selectedDevices", "enum", required: false, title: "Select Devices (${options.size() ?: 0} found)", multiple: true, options: options
    }
  }
}

def installed() {
  log.debug "Installed with settings: ${settings}"

  initialize()
}

def updated() {
  log.debug "Updated with settings: ${settings}"

  unsubscribe()
  initialize()
}

def initialize() {
  unsubscribe()
  unschedule()

  ssdpSubscribe()

  if (selectedDevices) {
    addDevices()
  }

  runEvery5Minutes("ssdpDiscover")
}

void ssdpDiscover() {
  sendHubCommand(new physicalgraph.device.HubAction("lan discovery ${searchTarget}", physicalgraph.device.Protocol.LAN))
}

void ssdpSubscribe() {
  subscribe(location, "ssdpTerm.${searchTarget}", ssdpHandler)
}

Map verifiedDevices() {
  def devices = getVerifiedDevices()
  def map = [:]
  devices.each {
    def value = it.value.name ?: "UPnP Device ${it.value.ssdpUSN.split(':')[1][-3..-1]}"
    def key = it.value.mac
    map["${key}"] = value
  }
  map
}

void verifyDevices() {
  def devices = getDevices().findAll { it?.value?.verified != true }
  devices.each {
    int port = convertHexToInt(it.value.deviceAddress)
    String ip = convertHexToIP(it.value.networkAddress)
    String host = "${ip}:${port}"
    sendHubCommand(new physicalgraph.device.HubAction("""GET ${it.value.ssdpPath} HTTP/1.1\r\nHOST: $host\r\n\r\n""", physicalgraph.device.Protocol.LAN, host, [callback: deviceDescriptionHandler]))
  }
}

def getVerifiedDevices() {
  getDevices().findAll{ it.value.verified == true }
}

def getDevices() {
  if (!state.devices) {
    state.devices = [:]
  }
  state.devices
}

def addDevices() {
  def devices = getDevices()

  selectedDevices.each { dni ->
    def selectedDevice = devices.find { it.value.mac == dni }
    def d
    if (selectedDevice) {
      d = getChildDevices()?.find {
        it.deviceNetworkId == selectedDevice.value.mac
      }
    }

    if (!d) {
      log.debug "Creating Generic UPnP Device with dni: ${selectedDevice.value.mac}"
      addChildDevice("smartthings", "Generic UPnP Device", selectedDevice.value.mac, selectedDevice?.value.hub, [
        "label": selectedDevice?.value?.name ?: "Generic UPnP Device",
        "data": [
          "mac": selectedDevice.value.mac,
          "ip": selectedDevice.value.networkAddress,
          "port": selectedDevice.value.deviceAddress
        ]
      ])
    }
  }
}

def ssdpHandler(evt) {
  def description = evt.description
  def hub = evt?.hubId

  def parsedEvent = parseLanMessage(description)
  parsedEvent << ["hub":hub]

  def devices = getDevices()
  String ssdpUSN = parsedEvent.ssdpUSN.toString()
  if (devices."${ssdpUSN}") {
    def d = devices."${ssdpUSN}"
    if (d.networkAddress != parsedEvent.networkAddress || d.deviceAddress != parsedEvent.deviceAddress) {
      d.networkAddress = parsedEvent.networkAddress
      d.deviceAddress = parsedEvent.deviceAddress
      def child = getChildDevice(parsedEvent.mac)
      if (child) {
        child.sync(parsedEvent.networkAddress, parsedEvent.deviceAddress)
      }
    }
  } else {
    devices << ["${ssdpUSN}": parsedEvent]
  }
}

void deviceDescriptionHandler(physicalgraph.device.HubResponse hubResponse) {
  def body = hubResponse.xml
  def devices = getDevices()
  def device = devices.find { it?.key?.contains(body?.device?.UDN?.text()) }
  if (device) {
    device.value << [name: body?.device?.roomName?.text(), model:body?.device?.modelName?.text(), serialNumber:body?.device?.serialNum?.text(), verified: true]
  }
}

private Integer convertHexToInt(hex) {
  Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
  [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
