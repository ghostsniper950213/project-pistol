function getHttpAutoUrl() {
  let pathName = window.location.pathname.substring(1)
  let webName =
    pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'))
  let path_root =
    window.location.protocol +
    '//' +
    window.location.host +
    '/' +
    (webName ? webName + '/' : '')
  return path_root
}

function getWsAutoUrl() {
  let pathName = window.location.pathname.substring(1)
  let webName =
    pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'))
  let path_root =
    (window.location.protocol.endsWith('s:') ? 'wss://' : 'ws://') +
    window.location.host +
    '/' +
    (webName ? webName + '/' : '')
  return path_root
}

export default {

  /**
   * base url
   */
  baseRequestUrl: 'http://192.168.1.136:8800/',
  
  // baseRequestUrl: getHttpAutoUrl()
}