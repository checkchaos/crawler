var page = require('webpage').create(),
    system = require('system'),
    t, address;
//读取命令行参数，也就是js文件路径。
if (system.args.length === 1) {
    console.log('Usage: loadspeed.js <some URL>');
//这行代码很重要。凡是结束必须调用。否则phantomjs不会停止  
    phantom.exit();
}
page.settings.resourceTimeout = 10000;//超过10秒放弃加载
//此处是用来设置截图的参数。不截图没啥用  
page.viewportSize = {
    width: 1280,
    height: 800
};
t = Date.now();//看看加载需要多久。
address = system.args[1];
page.open(address, function (status) {
    if (status !== 'success') {
        console.log('FAIL to load the address');
    } else {
        t = Date.now() - t;
        console.log("-------------------------------");
        console.log(page.content);
        console.log("-------------------------------")
        setTimeout(function () {
            phantom.exit();
        }, 10000);
    }
    phantom.exit();
});  