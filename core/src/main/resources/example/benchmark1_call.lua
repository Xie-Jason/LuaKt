local System = require("java.lang.System")
local now = System.currentTimeMillis
print("now is", now())
print("start")
print(now)


local function empty()
    return
end

local start = now()
local count = 1000 * 1000 * 100
for j = 0,count do
    empty()
end
total = now() - start
print(total)