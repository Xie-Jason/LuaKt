-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  tb:lv3  cs:lv4  f:lv5  
print(string["sub"]("Hello", 2, 4))
print("Hello"["sub"]("Hello", 2, 4))
for w = 160, 186, 1 do
    -- w:lv3  
    print(w, (w - 105), tostring(((w / 100) ^ 2 * 20))["sub"](tostring(((w / 100) ^ 2 * 20)), 1, 4), "~", tostring(((w / 100) ^ 2 * 24))["sub"](tostring(((w / 100) ^ 2 * 24)), 1, 4))
end
for p, c in utf8["codes"]("Hello哈哈吼吼 WW") do
    -- p:lv3  c:lv4  
    print(p, c, utf8["char"](c))
end
print(math)
print(math["min"])
print(math["min"](10, 20))
local tb, cs = {}, "HHH"
local f = function(k, v)
    -- fnIdx : 0  UpValues : lv5$cs <- lv4$cs, lv6$tb <- lv3$tb 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  k:lv3  v:lv4  cs:lv5  tb:lv6  
    print(cs)
    tb["insert"](tb, k, v)
end
tb["add"] = function(self, k, v)
    -- fnIdx : 1 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  self:lv3  k:lv4  v:lv5  
    self[k] = v
end
f(11, 22)
print(f[11])
tb["add"](tb, 233, 233)
print(tb[233])
