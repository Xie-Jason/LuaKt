
local n1,n2 = 10,20

local f = function(j,k)
    if j >= 20 or k >= 20 then
        print(" >= 20")
    end
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

while n1 > 0 or n2 > 0 do
    n1 = n1 - 1
    n2 = n2 - 1
    if n1 == 2 or n2 == 2 then
        print(n1)
    end
end

local i = 5

while i > 0 do
    if i == 2 then
        break
    end
    print("---")
    i = i - 1
end

repeat
    i = i + 1
    print(i)
until i < 5

local n, l, j, k  = 10,20,30,40,50
print(n,l,j,k)

for idx = 0,5 do
    print(idx)
end

G_he = 1
print(G_he)

local t = {k1=2,k2=3,4,5}

for k,v in ipairs(t) do
    print(k.."->"..v)
    G_he = G_he + 3
    print(G_he)
end