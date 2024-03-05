
local c = 5

repeat
    print(c)
    c = c - 1
    if c == 1 then
        goto out
    end

until c > 0

::out::

print("end")

local f = function(n, ...)
    print("n, ... ->", n, ...)
    print("..., n ->", ..., n)
end

f(1)
f(1,2)
f(1,2,3)


local t = {}

t["K"] = "V"

t.haha = function(self, ...)
    print(...)
    local len = select("#", ...)
    if len > 1 then
        print(select(0, ...), "来了啊")
    end
end

local mt = {}
-- setmetatable(t, 223)

t:haha("HH","GG")

print("end")

for k,v in pairs(t) do
    print(k,"=>",v)
end