local getH = require('test11.lua')

print(getH())

local count = 5
for i = 0,5 do
    count = count - 1
    local getLoopIdx = function()
        print(i,"+",count,"=",(i+count))
    end
    getLoopIdx()
end

local n, i, j, k  = 10,20,30,40,50
print(n,i,j,k)

local function getTwo()
    return 114,514
end

local function needThree(n1,n2,n3)
    print("needThree -> ",n1,n2,n3)
end

needThree(getTwo())
needThree(getTwo(),22,33)

local n1,n2 = 10,20

local f = function(j,k)
    if j < k then
        print(j..' < '..k)
    elseif j > k then
        print(j..' > '..k)
    else
        print(j..' = '..k)
    end
end

f(n1,n2)
f(n2,n1)
f(n1,n1)
f(n2,n2)

local c = 5

repeat
    c = c - 1
until c > 0

print(c, "is a", type(c))

print(c + "11222")

while c < 10 do
    c = c + 1
    print("value is ", c)
end

Integer = require("java.lang.Integer")

print(Integer.MAX_VALUE)
print(Integer.valueOf("22222") + 11)
print(Integer.valueOf("22222"):equals(22222))
print(Integer.valueOf("22222") == 22222)

Long = require("java.lang.Long")

print(Long.valueOf("11"):equals(11))

ArrayList = require("java.util.ArrayList")

arr = ArrayList(10)

print(arr, type(arr))
arr:add(11)
arr:add("www")
arr:add(1,"↓")
print(arr)

System = require("java.lang.System")
print(System.currentTimeMillis())

print("complete")

