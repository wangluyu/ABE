$(function () {
    var table=$('#table').DataTable({
        ajax: {
            //指定数据源
            url: url+"/file/getFile"
        },
        //deferRender: true,
        //每页显示5条数据
        pageLength: 5,
        columns: [
            {
                "data": function(data){
                    if(data.dir == 1){
                        return "<a class='dir' data-path="+data.fileName+">"+"<i class='fa fa-folder-open text-warning target'></i>"+data.fileName+"</a>";
                    }else if(data.dir == 0){
                        var ext = data.extension.toLowerCase();
                        if($.inArray(ext,['zip','rar','7z','exe','cab','iso','tgz','jar','war']) >= 0){
                            var icon = '<i class="fa fa-file-archive-o text-muted"></i>';
                        }else if($.inArray(ext,['doc','docx','wps','wpt','dot','rtf','dotx','docm','dotm']) >= 0){
                            var icon = '<i class="fa fa-file-word-o text-info"></i>';
                        }else if($.inArray(ext,['xlsx','xls','xml','csv','xlt','rtf']) >= 0){
                            var icon = '<i class="fa fa-file-excel-o text-success"></i>';
                        }else if($.inArray(ext,['ppt','pptx','dps','dpt','pot','pps','pptm','potm','ppsm']) >= 0){
                            var icon = '<i class="fa fa-file-powerpoint-o text-danger"></i>';
                        }else if($.inArray(ext,['pdf']) >= 0){
                            var icon = '<i class="fa fa-file-pdf-o text-danger"></i>';
                        }else if($.inArray(ext,['txt']) >= 0){
                            var icon = '<i class="fa fa-file-text-o text-muted"></i>';
                        }else if($.inArray(ext,['bmp','jpg','png','gif','jpeg','svg','tiff','pcx','tga','exif','fpx','psd','cdr','pcd','dxf','ufo','eps','ai','raw']) >= 0){
                            var icon = '<i class="fa fa-file-image-o text-info"></i>';
                        }else if($.inArray(ext,['mp4','avi','mpeg','mpg','mov','mkv','flv','f4v','rmvb','swf']) >= 0){
                            var icon = '<i class="fa fa-file-video-o text-info"></i>';
                        }else if($.inArray(ext,['mp3','wma','cd','wave','midi','ape','flac','aac']) >= 0){
                            var icon = '<i class="fa fa-file-audio-o text-info"></i>';
                        }else if($.inArray(ext,['html','htm','java','php','h','pl','py','sql','c','cs','cpp','css','js','jsp','lua','json','asp','c++','go','lisp','class']) >= 0){
                            var icon = '<i class="fa fa fa-file-code-o text-info"></i>';
                        }else{
                            var icon = '<i class="fa fa-file-o text-muted"></i>';
                        }
                        return icon+data.fileName;
                    }else{
                        return "error";
                    }
                }
            },
            {
                "data": "fileSize"
            },
            {
                "data": "createTime"
            },
            {
                "data": function(data){
                    if(data.dir == 1){
                        return "";
                    }else if(data.dir == 0){
                        return '<button type="button" class="btn btn-primary decrypt" data-file-name="'+data.fileName+'" data-file-date="'+data.createTime+'">解密</button>';
                    }else{
                        return "error";
                    }
                }
            }],
        processing: true,
        lengthChange: false,
        searching: false,
        //国际化设置（设置中文显示）
        language : {
            Processing: "处理中...",
            // search : '搜索：',//右上角的搜索文本，可以写html标签
            // searchPlaceholder: '请输入',
            paginate : {//分页的样式内容。
                previous : "上一页",
                next : "下一页",
                first : "第一页",
                last : "最后"
            },
            zeroRecords : "没有内容",//table tbody内容为空时，tbody的内容。
            //下面三者构成了总体的左下角的内容。
            info : "共 _PAGES_ 页，显示第 _START_ 条到第 _END_ 条，共 _TOTAL_ 条 ",//左下角的信息显示，大写的词为关键字。
            infoEmpty : "0条记录",//筛选为空时左下角的显示。
            infoFiltered : ""//筛选之后的左下角筛选提示，
        }
    });
    $('#table').on("click",".dir",function(){
        var filepath= $(this).data('path');
        console.log(filepath);
        var param = {
            "filePath": filepath,
        };
        table.settings()[0].ajax.data = param;
        table.ajax.reload();
    }).on("click",".decrypt",function(){
        // alert($(this).data('file-date').replace(/-/g,''));
        var decryptFrom = $('#decryptFrom');
        var encDateInput = $("<input></input>");
        encDateInput.attr('type','hidden');
        encDateInput.attr('id','encDate');
        encDateInput.attr('class','extra');
        encDateInput.attr('value',$(this).data('file-date').replace(/-/g,''));
        decryptFrom.append(encDateInput);   //将查询参数控件提交到表单上

        var encNameInput = $("<input></input>");
        encNameInput.attr('type','hidden');
        encNameInput.attr('id','encName');
        encNameInput.attr('class','extra');
        encNameInput.attr('value',$(this).data('file-name'));
        decryptFrom.append(encNameInput);   //将查询参数控件提交到表单上

        $('#decryptModal').modal('show');
    });

    var decryptFileInput = new FileInput();
    decryptFileInput.Init("decrypt", "/file/decrypt","decryptFrom",3,false);
    $('#decryptSubmit').click(function () {
        $('#decrypt').fileinput('upload');
    });

    var encryptFileInput = new FileInput();
    encryptFileInput.Init("encrypt", "/file/encrypt","encryptFrom",10,true,table);
    $('#encryptSubmit').click(function () {
        $('#encrypt').fileinput('upload');
    });
});
