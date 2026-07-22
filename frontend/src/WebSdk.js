// Proxy to intercept any property access and forward it to window.WebSdk
const WebSdkProxy = new Proxy({}, {
  get: function(target, prop) {
    if (typeof window !== 'undefined' && window.WebSdk) {
      return window.WebSdk[prop];
    }
    return undefined;
  }
});

export default WebSdkProxy;
// Ensure WebChannelClient is also available via the Proxy in case it's destructured
export const WebChannelClient = new Proxy(function(){}, {
  construct(target, args) {
    if (typeof window !== 'undefined' && window.WebSdk && window.WebSdk.WebChannelClient) {
      return new window.WebSdk.WebChannelClient(...args);
    }
    throw new Error("window.WebSdk.WebChannelClient is not defined");
  }
});
