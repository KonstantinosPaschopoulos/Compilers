@.testF_vtable = global [0 x i8*] []

@.testFac_vtable = global [1 x i8*] [
    i8* bitcast (i32 (i8*)* @testFac.ComputeFac to i8*)
]

declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
@_cNSZ = constant [15 x i8] c"Negative size\0a\00"

define void @print_int(i32 %i) {
    %_str = bitcast [4 x i8]* @_cint to i8*
    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
    ret void
}

define void @throw_oob() {
    %_str = bitcast [15 x i8]* @_cOOB to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define void @throw_nsz() {
    %_str = bitcast [15 x i8]* @_cNSZ to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define i32 @main() {
	%_0 = call i8* @calloc(i32 1, i32 8)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.testFac_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32 (i8*)* 
	%_8 = call i32 %_7(i8* %_0)
	call void (i32) @print_int(i32 %_8)
	ret i32 0
}

define i32 @testFac.ComputeFac(i8* %this) {
	%testing = alloca i8*
	%_9 = add i32 4, 20
	%_10 = icmp sge i32 %_9, 4
	br i1 %_10, label %label_expr0, label %label_expr1

	label_expr1:
	call void @throw_nsz()
	br label %label_expr0

	label_expr0:
	%_11 = call i8* @calloc(i32 %_9, i32 1)
	%_12 = bitcast i8* %_11 to i32*
	store i32 20, i32* %_12
	store i8* %_11, i8** %testing
	%_13 = load i8*, i8** %testing
	%_14 = bitcast i8* %_13 to i32*
	%_15 = load i32, i32* %_14
	ret i32 %_15
}

