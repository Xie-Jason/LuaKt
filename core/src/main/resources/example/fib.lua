
print("Hello fib")
System = require("java.lang.System")
local now = System.currentTimeMillis

local start = now()

function fib(n)
    if n == 1 or n == 2 then
        return 1
    else
        return fib(n-1) + fib(n-2)
    end
end

local num = tonumber(arg[0]) or 30

print("数字"..num.."的fib结果为",fib(num))
print("用时"..(now() - start).."ms")