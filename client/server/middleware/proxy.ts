import { proxyRequest } from 'h3'

export default defineEventHandler(async event => {
  const gatewayHost = process.env.GATEWAY_HOST || 'localhost'
  const proxyUrl = new URL(`http://${gatewayHost}:8080`)

  const url = event.node.req.url

  if (url && url.startsWith('/api/proxy')) {
    const urlWithoutProxy = url.replace('/api/proxy', '/api')
    await proxyRequest(event, `${proxyUrl.origin}${urlWithoutProxy}`, {
      headers: {
        host: proxyUrl.host
      }
    })
  }
})
