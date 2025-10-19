

streamlit Please wait



streamlit 通过 --server.port 修改端口之后出现 Please wait...  问题



修改端口之后，发现页面始终 Please wait，通过 F12 查看发现，其中 _store/health  _store/allowed-message-origins 的端口仍然为 8501，导致页面加载不出来。



解决办法：就是不要改端口。

