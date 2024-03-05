
print(string.sub("Hello", 2, 4))
print("Hello":sub(2,4))

for w = 160, 186 do
    print(w, w - 105, tostring((w / 100) ^ 2 * 20):sub(1,4), "~", tostring((w / 100) ^ 2 * 24):sub(1,4))
end

for p,c in utf8.codes("Hello哈哈吼吼 WW") do
    print(p, c, utf8.char(c))
end

print(math)
print(math.min)
print(math.min(10,20))

local tb, cs = {}, "HHH"
local function f(k,v)
    print(cs)
    tb:insert(k,v)
end

function tb:add(k,v)
    self[k] = v
end

f(11,22)
print(f[11])

tb:add(233,233)
print(tb[233])